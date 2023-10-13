package ru.astrainteractive.astrashop.gui.shops

import ru.astrainteractive.astrashop.State
import ru.astrainteractive.astrashop.api.util.SpigotShopConfigAlias

sealed interface ShopsState : State {
    data object Loading : ShopsState
    class Loaded(val shops: List<SpigotShopConfigAlias>) : ShopsState
}
