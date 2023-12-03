package ru.astrainteractive.astrashop.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.plugin.Plugin
import ru.astrainteractive.astralibs.filemanager.DefaultSpigotFileManager
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.api.util.ShopItemParser
import ru.astrainteractive.astrashop.api.util.getYmlFiles

internal class SpigotShopApi(private val plugin: Plugin) : ShopApi {
    private val shopItemParser = ShopItemParser(plugin)
    private val readerDispatcher = Dispatchers.IO.limitedParallelism(1)

    override suspend fun fetchShopList(): List<ShopConfig> {
        return withContext(readerDispatcher) {
            getYmlFiles(plugin)?.mapNotNull(shopItemParser::parseShopFileOrNull) ?: emptyList()
        }
    }

    override suspend fun fetchShop(shopFileName: String): ShopConfig {
        return withContext(readerDispatcher) {
            DefaultSpigotFileManager(plugin, shopFileName).let(shopItemParser::parseShopFile)
        }
    }

    override suspend fun updateShop(shopConfig: ShopConfig) {
        return withContext(readerDispatcher) {
            shopItemParser.saveItem(shopConfig)
        }
    }
}
