package ru.astrainteractive.astrashop.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.plugin.Plugin
import ru.astrainteractive.astralibs.file_manager.FileManager
import ru.astrainteractive.astrashop.domain.models.ShopConfig
import ru.astrainteractive.astrashop.domain.utils.ShopItemParser
import ru.astrainteractive.astrashop.domain.utils.getYmlFiles

class SpigotDataSource(private val plugin: Plugin) : IDataSource {
    private val shopItemParser = ShopItemParser(plugin)
    private val readerDispatcher = Dispatchers.IO.limitedParallelism(1)

    override suspend fun fetchShopList(): List<ShopConfig> = withContext(readerDispatcher) {
        getYmlFiles(plugin)?.mapNotNull(shopItemParser::parseShopFileOrNull) ?: emptyList()
    }

    override suspend fun fetchShop(configName: String): ShopConfig = withContext(readerDispatcher) {
        FileManager(plugin, configName).let(shopItemParser::parseShopFile)
    }

    override suspend fun updateShop(shopConfig: ShopConfig) = withContext(readerDispatcher) {
        shopItemParser.saveItem(shopConfig)
    }
}