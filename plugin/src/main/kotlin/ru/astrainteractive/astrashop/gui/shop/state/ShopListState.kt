package ru.astrainteractive.astrashop.gui.shop.state

import ru.astrainteractive.astrashop.domain.models.ShopConfig

sealed class ShopListState(val items: HashMap<String, ShopConfig.ShopItem>) {
    object Loading : ShopListState(HashMap())
    data class List(val config: ShopConfig) : ShopListState(config.items)

    data class ListEditMode(
        val config: ShopConfig,
        val clickedSlot: Int,
    ) : ShopListState(config.items)
}
