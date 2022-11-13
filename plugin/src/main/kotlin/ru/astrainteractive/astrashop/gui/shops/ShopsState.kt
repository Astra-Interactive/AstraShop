package ru.astrainteractive.astrashop.gui.shops

import ru.astrainteractive.astrashop.domain.models.ShopConfig
import ru.astrainteractive.astrashop.State

sealed interface ShopsState : State {
    object Loading : ShopsState
    class Loaded(val shops: List<ShopConfig>) : ShopsState
}