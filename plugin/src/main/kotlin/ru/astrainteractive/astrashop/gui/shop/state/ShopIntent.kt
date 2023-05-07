package ru.astrainteractive.astrashop.gui.shop.state

import org.bukkit.event.inventory.InventoryClickEvent
import ru.astrainteractive.astrashop.domain.models.ShopConfig
import ru.astrainteractive.astrashop.domain.models.SpigotShopItem
import ru.astrainteractive.astrashop.domain.models.SpigotTitleItem
import ru.astrainteractive.astrashop.gui.ShopPlayerHolder
import ru.astrainteractive.astrashop.gui.shop.ShopGUI

sealed interface ShopIntent {
    class OpenShops(val playerHolder: ShopPlayerHolder) : ShopIntent
    class InventoryClick(val e: InventoryClickEvent) : ShopIntent {
        fun isShopGUI() = e.clickedInventory?.holder is ShopGUI
        fun isPlayerInventory() = e.clickedInventory?.holder == e.whoClicked.inventory.holder
    }

    class DeleteItem(
        val e: InventoryClickEvent,
        private val isRightClick: Boolean,
        private val isShiftClick: Boolean,
    ) : ShopIntent {
        fun isValid() = isRightClick && isShiftClick && e.clickedInventory?.holder is ShopGUI
    }

    class OpenBuyGui(
        val shopConfig: ShopConfig<SpigotTitleItem, SpigotShopItem>,
        val shopItem: ShopConfig.ShopItem<SpigotShopItem>,
        val playerHolder: ShopPlayerHolder,
        private val isLeftClick: Boolean,
        private val isShiftClick: Boolean,
        private val currentState: ShopListState
    ) : ShopIntent {
        fun isValid() = isLeftClick && !isShiftClick && currentState is ShopListState.List
    }

    object ToggleEditModeClick : ShopIntent
}
