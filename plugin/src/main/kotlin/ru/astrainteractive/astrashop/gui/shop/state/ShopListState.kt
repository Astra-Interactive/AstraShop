package ru.astrainteractive.astrashop.gui.shop.state

import org.bukkit.event.inventory.InventoryClickEvent
import ru.astrainteractive.astrashop.State
import ru.astrainteractive.astrashop.domain.models.ShopConfig
import ru.astrainteractive.astrashop.domain.models.SpigotShopItem
import ru.astrainteractive.astrashop.domain.models.SpigotTitleItem
import ru.astrainteractive.astrashop.domain.utils.SpigotShopConfigAlias
import ru.astrainteractive.astrashop.domain.utils.SpigotShopItemAlias

sealed class ShopListState(val items: HashMap<String, SpigotShopItemAlias>): State {
    object Loading : ShopListState(HashMap())
    data class List(val config: SpigotShopConfigAlias) : ShopListState(config.items)

    data class ListEditMode(
        val config: SpigotShopConfigAlias,
        val clickEvent: InventoryClickEvent? = null,
        val oldClickedItemPage:Int? = null
    ) : ShopListState(config.items)
}

