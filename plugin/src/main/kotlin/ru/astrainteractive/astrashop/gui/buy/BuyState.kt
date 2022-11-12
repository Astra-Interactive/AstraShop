package ru.astrainteractive.astrashop.gui.buy

import ru.astrainteractive.astrashop.domain.models.ShopConfig

sealed interface BuyState {
    object Loading : BuyState

    data class Loaded(
        val item: ShopConfig.ShopItem,
        val shopConfig: ShopConfig.Options,
        val instance: ShopConfig
    ) : BuyState
}
