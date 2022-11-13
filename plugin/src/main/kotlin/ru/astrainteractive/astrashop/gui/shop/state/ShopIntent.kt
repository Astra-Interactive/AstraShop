package ru.astrainteractive.astrashop.gui.shop.state

import org.bukkit.event.inventory.InventoryClickEvent
import ru.astrainteractive.astrashop.domain.models.ShopConfig
import ru.astrainteractive.astrashop.gui.PlayerHolder

sealed interface ShopIntent {
    class OpenShops(val playerHolder: PlayerHolder) : ShopIntent

    object ExitEditMode : ShopIntent

    class OpenBuyGui(
        val shopConfig: ShopConfig,
        val shopItem: ShopConfig.ShopItem,
        val playerHolder: PlayerHolder,
        private val isLeftClick: Boolean,
        private val isShiftClick: Boolean,
        private val currentState: ShopListState
    ) : ShopIntent {
        fun isValid() = isLeftClick && !isShiftClick && currentState is ShopListState.List
    }

    class EditModeClick(val e: InventoryClickEvent) : ShopIntent
}