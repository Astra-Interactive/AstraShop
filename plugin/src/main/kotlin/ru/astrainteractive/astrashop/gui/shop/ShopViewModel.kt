package ru.astrainteractive.astrashop.gui.shop

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.bukkit.event.inventory.InventoryClickEvent
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.getValue
import ru.astrainteractive.astrashop.asState
import ru.astrainteractive.astrashop.di.impl.RootModuleImpl
import ru.astrainteractive.astrashop.gui.buy.BuyGUI
import ru.astrainteractive.astrashop.gui.shop.state.ShopIntent
import ru.astrainteractive.astrashop.gui.shop.state.ShopListState
import ru.astrainteractive.astrashop.gui.shops.ShopsGUI
import ru.astrainteractive.astrashop.utils.asShopItem
import ru.astrainteractive.astrashop.utils.openOnMainThread

class ShopViewModel(
    private val configName: String,
    private val pagingProvider: PagingProvider
) : AsyncComponent() {
    private val dataSource by RootModuleImpl.spigotShopApi
    private val translation by RootModuleImpl.translation

    val state = MutableStateFlow<ShopListState>(ShopListState.Loading)
    val maxItemsAmount: Int
        get() = state.value.items.keys.mapNotNull { it.toIntOrNull() }.maxOrNull() ?: 0

    private fun load() = componentScope.launch(Dispatchers.IO) {
        val oldState = state.value
        state.value = ShopListState.Loading
        val config = dataSource.fetchShop(configName)
        state.value = when (oldState) {
            is ShopListState.List, is ShopListState.Loading -> ShopListState.List(config)
            is ShopListState.ListEditMode -> ShopListState.ListEditMode(config)
        }
    }

    private fun onRemoveItemClicked(intent: ShopIntent.DeleteItem) {
        if (!intent.isValid()) return
        val state = state.value.asState<ShopListState.List>() ?: return
        state.config.items.remove(pagingProvider.index(intent.e.slot).toString())
        componentScope.launch(Dispatchers.IO) {
            dataSource.updateShop(state.config)
            load()
        }
    }

    private fun onSetItemClicked(state: ShopListState.ListEditMode, newEvent: InventoryClickEvent) {
        val oldEvent = state.clickEvent ?: return
        val shopItemIndex = pagingProvider.index(oldEvent.slot, page = state.oldClickedItemPage ?: pagingProvider.page)
        val shopItem = state.config.items[shopItemIndex.toString()]

        val isNewClickOnPlayerInventory = newEvent.clickedInventory?.holder == newEvent.whoClicked.inventory.holder

        if (isNewClickOnPlayerInventory) {
            val newItem = newEvent.currentItem ?: return
            state.config.items[shopItemIndex.toString()] = newItem.asShopItem(shopItemIndex)
        } else {
            val clickedShopItemIndex = pagingProvider.index(newEvent.slot)
            val clickedItem = state.config.items[clickedShopItemIndex.toString()]

            if (clickedItem == null) {
                state.config.items.remove(shopItemIndex.toString())
            } else {
                state.config.items[shopItemIndex.toString()] = clickedItem
            }

            if (shopItem == null) {
                state.config.items.remove(clickedShopItemIndex.toString())
            } else {
                state.config.items[clickedShopItemIndex.toString()] = shopItem
            }
        }
        componentScope.launch(Dispatchers.IO) {
            dataSource.updateShop(state.config)
            load()
        }
    }

    private fun enterEditMode() {
        val state = state.value.asState<ShopListState.List>() ?: return
        this.state.value = ShopListState.ListEditMode(state.config)
    }

    private fun exitEditMode() {
        val state = state.value as? ShopListState.ListEditMode ?: return
        this.state.value = ShopListState.List(state.config)
    }

    fun onIntent(intent: ShopIntent) = componentScope.launch(Dispatchers.IO) {
        when (intent) {
            is ShopIntent.OpenShops -> {
                ShopsGUI(intent.playerHolder).openOnMainThread()
            }

            is ShopIntent.OpenBuyGui -> {
                if (!intent.isValid()) return@launch
                BuyGUI(intent.shopConfig, intent.shopItem, intent.playerHolder).openOnMainThread()
            }

            is ShopIntent.ToggleEditModeClick -> {
                if (state.value is ShopListState.ListEditMode) {
                    exitEditMode()
                } else {
                    enterEditMode()
                }
            }

            is ShopIntent.DeleteItem -> onRemoveItemClicked(intent)
            is ShopIntent.InventoryClick -> {
                val state = state.value.asState<ShopListState.ListEditMode>() ?: return@launch
                if (state.clickEvent == null && intent.isShopGUI()) {
                    this@ShopViewModel.state.value =
                        state.copy(clickEvent = intent.e, oldClickedItemPage = pagingProvider.page)
                } else if (state.clickEvent != null) {
                    onSetItemClicked(state, intent.e)
                }
            }
        }
    }

    init {
        load()
    }
}
