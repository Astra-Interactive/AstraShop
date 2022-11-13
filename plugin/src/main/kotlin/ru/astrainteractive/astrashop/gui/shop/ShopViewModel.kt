package ru.astrainteractive.astrashop.gui.shop

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.bukkit.event.inventory.InventoryClickEvent
import ru.astrainteractive.astralibs.architecture.ViewModel
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astrashop.gui.buy.BuyGUI
import ru.astrainteractive.astrashop.gui.shop.state.ShopIntent
import ru.astrainteractive.astrashop.gui.shop.state.ShopListState
import ru.astrainteractive.astrashop.gui.shops.ShopsGUI
import ru.astrainteractive.astrashop.modules.DataSourceModule
import ru.astrainteractive.astrashop.modules.TranslationModule
import ru.astrainteractive.astrashop.utils.AstraPermission
import ru.astrainteractive.astrashop.utils.asShopItem

class ShopViewModel(private val configName: String, private val pagingProvider: PagingProvider) : ViewModel() {
    val state = MutableStateFlow<ShopListState>(ShopListState.Loading)
    val maxItemsAmount: Int
        get() = state.value.items.keys.mapNotNull { it.toIntOrNull() }.maxOrNull() ?: 0
    private val dataSource by DataSourceModule
    private val translation by TranslationModule

    private fun load() = viewModelScope.launch(Dispatchers.IO) {
        state.value = ShopListState.Loading
        val config = dataSource.fetchShop(configName)
        state.value = ShopListState.List(config)
    }

    private fun onRemoveItemClicked(state: ShopListState.ListEditMode, e: InventoryClickEvent) {
        if (!e.isShiftClick && !e.isRightClick && e.clickedInventory?.holder !is ShopGUI) return

        state.config.items.remove(pagingProvider.index(e.slot).toString())
        viewModelScope.launch(Dispatchers.IO) {
            dataSource.updateShop(state.config)
            load()
        }
    }

    private fun onSetItemClicked(state: ShopListState.ListEditMode, e: InventoryClickEvent) {
        if (e.clickedInventory?.holder is ShopGUI) return
        val clickedItem = e.currentItem ?: return

        val index = pagingProvider.index(state.clickedSlot)
        state.config.items[index.toString()] = clickedItem.asShopItem(index)
        viewModelScope.launch(Dispatchers.IO) {
            dataSource.updateShop(state.config)
            load()
        }
    }


    private fun onEditStateClicked(state: ShopListState.ListEditMode, e: InventoryClickEvent) {
        onRemoveItemClicked(state, e)
        onSetItemClicked(state, e)
    }

    private fun startEditMode(state: ShopListState.List, e: InventoryClickEvent) {
        if (!AstraPermission.EditShop.hasPermission(e.whoClicked)) {
            e.whoClicked.sendMessage(translation.noPermission)
            return
        }
        if (!e.isRightClick) return
        if (e.clickedInventory?.holder !is ShopGUI) return
        this.state.value = ShopListState.ListEditMode(state.config, e.slot)
    }


    private fun onEditModeClick(e: InventoryClickEvent) {
        when (val state = state.value) {
            is ShopListState.ListEditMode -> onEditStateClicked(state, e)
            is ShopListState.List -> startEditMode(state, e)
            ShopListState.Loading -> Unit
        }
    }

    private fun exitEditMode() {
        val state = state.value as? ShopListState.ListEditMode ?: return
        this.state.value = ShopListState.List(state.config)
    }

    fun onIntent(intent: ShopIntent) = viewModelScope.launch(Dispatchers.IO) {
        when (intent) {
            is ShopIntent.OpenShops -> ShopsGUI(intent.playerHolder.player).open()
            is ShopIntent.OpenBuyGui -> {
                if (!intent.isValid()) return@launch
                BuyGUI(intent.shopConfig, intent.shopItem, intent.playerHolder.player)
            }

            ShopIntent.ExitEditMode -> exitEditMode()
            is ShopIntent.EditModeClick -> onEditModeClick(intent.e)
        }
    }

    init {
        load()
    }
}