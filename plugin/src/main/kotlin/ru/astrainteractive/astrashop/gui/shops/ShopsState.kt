package ru.astrainteractive.astrashop.gui.shops

import ru.astrainteractive.astrashop.domain.models.ShopConfig
import ru.astrainteractive.astrashop.State
import ru.astrainteractive.astrashop.domain.models.SpigotShopItem
import ru.astrainteractive.astrashop.domain.models.SpigotTitleItem
import ru.astrainteractive.astrashop.domain.utils.SpigotShopConfigAlias

sealed interface ShopsState : State {
    object Loading : ShopsState
    class Loaded(val shops: List<SpigotShopConfigAlias>) : ShopsState
}