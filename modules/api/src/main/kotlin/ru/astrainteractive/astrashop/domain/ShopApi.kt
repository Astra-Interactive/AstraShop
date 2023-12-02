package ru.astrainteractive.astrashop.domain

import ru.astrainteractive.astrashop.domain.model.ShopConfig

interface ShopApi {
    suspend fun fetchShopList(): List<ShopConfig>

    suspend fun fetchShop(configName: String): ShopConfig

    suspend fun updateShop(shopConfig: ShopConfig)
}
