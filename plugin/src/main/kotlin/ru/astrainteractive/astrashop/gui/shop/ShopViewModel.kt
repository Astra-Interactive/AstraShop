package ru.astrainteractive.astrashop.gui.shop

import ru.astrainteractive.astralibs.architecture.ViewModel
import ru.astrainteractive.astrashop.domain.models.ShopConfig

class ShopViewModel(val shopConfig: ShopConfig) : ViewModel() {
    val items = shopConfig.items

}