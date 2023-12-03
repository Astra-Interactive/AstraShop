package ru.astrainteractive.astrashop.api

import ru.astrainteractive.astrashop.api.model.ShopConfig

interface ShopApi {
    suspend fun fetchShopList(): List<ShopConfig>

    suspend fun fetchShop(shopFileName: String): ShopConfig

    suspend fun updateShop(shopConfig: ShopConfig)
}
