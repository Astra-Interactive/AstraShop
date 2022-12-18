package ru.astrainteractive.astrashop.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.astrainteractive.astralibs.file_manager.FileManager
import ru.astrainteractive.astrashop.domain.models.ShopConfig
import ru.astrainteractive.astrashop.domain.utils.ShopItemParser
import ru.astrainteractive.astrashop.domain.utils.getYmlFiles

class SpigotDataSource : IDataSource {
    private val readerDispatcher = Dispatchers.IO.limitedParallelism(1)

    override suspend fun fetchShopList(): List<ShopConfig> = withContext(readerDispatcher) {
        getYmlFiles()?.mapNotNull(ShopItemParser::parseShopFileOrNull) ?: emptyList()
    }

    override suspend fun fetchShop(configName: String): ShopConfig = withContext(readerDispatcher) {
        FileManager(configName).let(ShopItemParser::parseShopFile)
    }

    override suspend fun updateShop(shopConfig: ShopConfig) = withContext(readerDispatcher) {
        ShopItemParser.saveItem(shopConfig)
    }
}