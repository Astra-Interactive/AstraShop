package ru.astrainteractive.astrashop.gui.buy

import ru.astrainteractive.astrashop.State
import ru.astrainteractive.astrashop.domain.model.ShopConfig

sealed interface BuyState : State {
    data object Loading : BuyState

    data class Loaded(
        val item: ShopConfig.ShopItem,
        val shopConfig: ShopConfig.Options,
        val instance: ShopConfig,
        val playerBalance: Int
    ) : BuyState
}
