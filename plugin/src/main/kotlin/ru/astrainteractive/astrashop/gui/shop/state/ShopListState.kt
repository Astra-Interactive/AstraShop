package ru.astrainteractive.astrashop.gui.shop.state

import org.bukkit.event.inventory.InventoryClickEvent
import ru.astrainteractive.astrashop.State
import ru.astrainteractive.astrashop.domain.models.ShopConfig

sealed class ShopListState(val items: HashMap<String, ShopConfig.ShopItem>): State {
    object Loading : ShopListState(HashMap())
    data class List(val config: ShopConfig) : ShopListState(config.items)

    data class ListEditMode(
        val config: ShopConfig,
        val clickEvent: InventoryClickEvent? = null,
        val oldClickedItemPage:Int? = null
    ) : ShopListState(config.items)
}
