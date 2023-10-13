@file:Suppress("Filename")

package ru.astrainteractive.astrashop.api.util

import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.api.model.SpigotShopItem
import ru.astrainteractive.astrashop.api.model.SpigotTitleItem

typealias SpigotShopConfigAlias = ShopConfig<SpigotTitleItem, SpigotShopItem>
typealias SpigotShopOptionsAlias = ShopConfig.Options<SpigotTitleItem>
typealias SpigotShopItemAlias = ShopConfig.ShopItem<SpigotShopItem>
