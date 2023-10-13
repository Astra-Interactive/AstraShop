package ru.astrainteractive.astrashop.gui.buy

import ru.astrainteractive.astrashop.State
import ru.astrainteractive.astrashop.api.util.SpigotShopConfigAlias
import ru.astrainteractive.astrashop.api.util.SpigotShopItemAlias
import ru.astrainteractive.astrashop.api.util.SpigotShopOptionsAlias

sealed interface BuyState : State {
    data object Loading : BuyState

    data class Loaded(
        val item: SpigotShopItemAlias,
        val shopConfig: SpigotShopOptionsAlias,
        val instance: SpigotShopConfigAlias,
        val playerBalance: Int
    ) : BuyState
}
