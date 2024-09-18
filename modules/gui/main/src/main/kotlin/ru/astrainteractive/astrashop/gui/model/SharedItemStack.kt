package ru.astrainteractive.astrashop.gui.model

import ru.astrainteractive.astrashop.api.model.ShopConfig

interface SharedItemStack {
    fun isSimilar(shopItem: ShopConfig.ShopItem): Boolean
}
