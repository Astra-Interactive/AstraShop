package ru.astrainteractive.astrashop.gui.shop.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import org.bukkit.event.inventory.InventoryClickEvent
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.gui.model.ShopPlayerHolder
import ru.astrainteractive.astrashop.gui.shop.ui.ShopGUI

interface ShopComponent : CoroutineScope {
    val model: StateFlow<Model>

    val maxItemsAmount: Int

    fun onIntent(intent: Intent)

    sealed class Model(val items: HashMap<String, ShopConfig.ShopItem>) {
        data object Loading : Model(HashMap())
        data class List(val config: ShopConfig) : Model(config.items)

        data class ListEditMode(
            val config: ShopConfig,
            val clickEvent: InventoryClickEvent? = null,
            val oldClickedItemPage: Int? = null
        ) : Model(config.items)
    }

    sealed interface Intent {
        class OpenShops(val playerHolder: ShopPlayerHolder) : Intent
        class InventoryClick(val e: InventoryClickEvent) : Intent {
            fun isShopGUI() = e.clickedInventory?.holder is ShopGUI
            fun isPlayerInventory() = e.clickedInventory?.holder == e.whoClicked.inventory.holder
        }

        class DeleteItem(
            val e: InventoryClickEvent,
            private val isRightClick: Boolean,
            private val isShiftClick: Boolean,
        ) : Intent {
            fun isValid() = isRightClick && isShiftClick && e.clickedInventory?.holder is ShopGUI
        }

        class OpenBuyGui(
            val shopConfig: ShopConfig,
            val shopItem: ShopConfig.ShopItem,
            val playerHolder: ShopPlayerHolder,
            private val isLeftClick: Boolean,
            private val isShiftClick: Boolean,
            private val currentState: Model
        ) : Intent {
            fun isValid() = isLeftClick && !isShiftClick && currentState is Model.List
        }

        data object ToggleEditModeClick : Intent
    }
}
