package ru.astrainteractive.astrashop.domain

import ru.astrainteractive.astrashop.domain.models.ShopConfig

interface IDataSource {
    suspend fun fetchShopList(): List<ShopConfig>

    suspend fun fetchShop(configName: String): ShopConfig

    suspend fun updateShop(shopConfig: ShopConfig)
}