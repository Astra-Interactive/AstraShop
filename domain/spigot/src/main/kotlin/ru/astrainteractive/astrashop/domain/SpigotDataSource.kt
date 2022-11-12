package ru.astrainteractive.astrashop.domain

import ru.astrainteractive.astralibs.file_manager.FileManager
import ru.astrainteractive.astrashop.domain.models.ShopConfig
import ru.astrainteractive.astrashop.domain.utils.ShopItemParser
import ru.astrainteractive.astrashop.utils.getYmlFiles

class SpigotDataSource : IDataSource {

    override suspend fun fetchShopList(): List<ShopConfig> {
        return getYmlFiles()?.mapNotNull(ShopItemParser::parseShopFileOrNull) ?: emptyList()
    }

    override suspend fun fetchShop(configName: String): ShopConfig {
        return FileManager(configName).let(ShopItemParser::parseShopFile)
    }

    override suspend fun updateShop(shopConfig: ShopConfig) {
        ShopItemParser.saveItem(shopConfig)
    }
}