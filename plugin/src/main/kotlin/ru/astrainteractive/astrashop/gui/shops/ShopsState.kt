package ru.astrainteractive.astrashop.gui.shops

import ru.astrainteractive.astrashop.State
import ru.astrainteractive.astrashop.domain.model.ShopConfig

sealed interface ShopsState : State {
    data object Loading : ShopsState
    class Loaded(val shops: List<ShopConfig>) : ShopsState
}
