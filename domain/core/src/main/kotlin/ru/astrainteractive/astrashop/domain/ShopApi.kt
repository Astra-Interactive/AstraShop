package ru.astrainteractive.astrashop.domain

import ru.astrainteractive.astrashop.domain.models.ShopConfig

interface ShopApi<TITLE_ITEM, SHOP_ITEM> {
    suspend fun fetchShopList(): List<ShopConfig<TITLE_ITEM, SHOP_ITEM>>

    suspend fun fetchShop(configName: String): ShopConfig<TITLE_ITEM, SHOP_ITEM>

    suspend fun updateShop(shopConfig: ShopConfig<TITLE_ITEM, SHOP_ITEM>)
}
