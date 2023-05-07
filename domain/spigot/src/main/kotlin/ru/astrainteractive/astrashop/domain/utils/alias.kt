package ru.astrainteractive.astrashop.domain.utils

import ru.astrainteractive.astrashop.domain.models.ShopConfig
import ru.astrainteractive.astrashop.domain.models.SpigotShopItem
import ru.astrainteractive.astrashop.domain.models.SpigotTitleItem

typealias SpigotShopConfigAlias = ShopConfig<SpigotTitleItem, SpigotShopItem>
typealias SpigotShopOptionsAlias = ShopConfig.Options<SpigotTitleItem>
typealias SpigotShopItemAlias = ShopConfig.ShopItem<SpigotShopItem>
