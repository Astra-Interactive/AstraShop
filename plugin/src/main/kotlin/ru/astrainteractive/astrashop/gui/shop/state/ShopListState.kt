package ru.astrainteractive.astrashop.gui.shop.state

import org.bukkit.event.inventory.InventoryClickEvent
import ru.astrainteractive.astrashop.State
import ru.astrainteractive.astrashop.api.util.SpigotShopConfigAlias
import ru.astrainteractive.astrashop.api.util.SpigotShopItemAlias

sealed class ShopListState(val items: HashMap<String, SpigotShopItemAlias>) : State {
    data object Loading : ShopListState(HashMap())
    data class List(val config: SpigotShopConfigAlias) : ShopListState(config.items)

    data class ListEditMode(
        val config: SpigotShopConfigAlias,
        val clickEvent: InventoryClickEvent? = null,
        val oldClickedItemPage: Int? = null
    ) : ShopListState(config.items)
}
