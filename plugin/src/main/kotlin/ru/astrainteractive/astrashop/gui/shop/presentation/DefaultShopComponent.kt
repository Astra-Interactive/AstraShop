package ru.astrainteractive.astrashop.gui.shop.presentation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.bukkit.event.inventory.InventoryClickEvent
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astrashop.api.ShopApi
import ru.astrainteractive.astrashop.gui.router.GuiRouter
import ru.astrainteractive.astrashop.gui.shop.presentation.ShopComponent.Intent
import ru.astrainteractive.astrashop.gui.shop.presentation.ShopComponent.Model
import ru.astrainteractive.astrashop.gui.shop.util.PagingProvider
import ru.astrainteractive.astrashop.gui.util.ItemStackGuiExt.asShopItem

class DefaultShopComponent(
    private val shopFileName: String,
    private val pagingProvider: PagingProvider,
    private val dataSource: ShopApi,
    private val router: GuiRouter
) : AsyncComponent(), ShopComponent {

    override val model = MutableStateFlow<Model>(Model.Loading)
    override val maxItemsAmount: Int
        get() = model.value.items.keys.mapNotNull { it.toIntOrNull() }.maxOrNull() ?: 0

    private fun load() = componentScope.launch(Dispatchers.IO) {
        val oldState = model.value
        model.value = Model.Loading
        val config = dataSource.fetchShop(shopFileName)
        model.value = when (oldState) {
            is Model.List, is Model.Loading -> Model.List(config)
            is Model.ListEditMode -> Model.ListEditMode(config)
        }
    }

    private fun onRemoveItemClicked(intent: Intent.DeleteItem) {
        if (!intent.isValid()) return
        val state = model.value as? Model.List ?: return
        state.config.items.remove(pagingProvider.index(intent.e.slot).toString())
        componentScope.launch(Dispatchers.IO) {
            dataSource.updateShop(state.config)
            load()
        }
    }

    private fun onSetItemClicked(state: Model.ListEditMode, newEvent: InventoryClickEvent) {
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
        val state = model.value as? Model.List ?: return
        this.model.value = Model.ListEditMode(state.config)
    }

    private fun exitEditMode() {
        val state = model.value as? Model.ListEditMode ?: return
        this.model.value = Model.List(state.config)
    }

    override fun onIntent(intent: Intent) {
        componentScope.launch(Dispatchers.IO) {
            when (intent) {
                is Intent.OpenShops -> {
                    val route = GuiRouter.Route.Shops(intent.playerHolder)
                    router.open(route)
                }

                is Intent.OpenBuyGui -> {
                    if (!intent.isValid()) return@launch
                    val route = GuiRouter.Route.Buy(
                        playerHolder = intent.playerHolder,
                        shopConfig = dataSource.fetchShop(shopFileName),
                        shopItem = intent.shopItem
                    )
                    router.open(route)
                }

                is Intent.ToggleEditModeClick -> {
                    if (model.value is Model.ListEditMode) {
                        exitEditMode()
                    } else {
                        enterEditMode()
                    }
                }

                is Intent.DeleteItem -> onRemoveItemClicked(intent)
                is Intent.InventoryClick -> {
                    val state = model.value as? Model.ListEditMode ?: return@launch
                    if (state.clickEvent == null && intent.isShopGUI()) {
                        this@DefaultShopComponent.model.value =
                            state.copy(clickEvent = intent.e, oldClickedItemPage = pagingProvider.page)
                    } else if (state.clickEvent != null) {
                        onSetItemClicked(state, intent.e)
                    }
                }
            }
        }
    }

    init {
        load()
    }
}
