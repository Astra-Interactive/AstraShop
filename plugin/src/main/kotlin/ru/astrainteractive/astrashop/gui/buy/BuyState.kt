package ru.astrainteractive.astrashop.gui.buy

import ru.astrainteractive.astrashop.State
import ru.astrainteractive.astrashop.domain.utils.SpigotShopConfigAlias
import ru.astrainteractive.astrashop.domain.utils.SpigotShopItemAlias
import ru.astrainteractive.astrashop.domain.utils.SpigotShopOptionsAlias

sealed interface BuyState : State {
    data object Loading : BuyState

    data class Loaded(
        val item: SpigotShopItemAlias,
        val shopConfig: SpigotShopOptionsAlias,
        val instance: SpigotShopConfigAlias,
        val playerBalance: Int
    ) : BuyState
}
