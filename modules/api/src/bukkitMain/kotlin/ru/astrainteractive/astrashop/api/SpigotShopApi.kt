package ru.astrainteractive.astrashop.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.plugin.Plugin
import ru.astrainteractive.astralibs.filemanager.DefaultSpigotFileManager
import ru.astrainteractive.astralibs.filemanager.SpigotFileManager
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.api.parser.ShopItemParserImpl
import ru.astrainteractive.astrashop.api.parser.util.getYmlFiles

internal class SpigotShopApi(
    private val plugin: Plugin,
    private val shopItemParser: ShopItemParserImpl
) : ShopApi {
    private val limitedDispatcher = Dispatchers.IO.limitedParallelism(1)

    private fun shopFileOrNull(fileManager: SpigotFileManager): ShopConfig? {
        return runCatching { shopItemParser.parseShopFile(fileManager) }
            .onFailure(Throwable::printStackTrace)
            .getOrNull()
    }

    override suspend fun fetchShopList(): List<ShopConfig> {
        return withContext(limitedDispatcher) {
            getYmlFiles(plugin)
                .mapNotNull(::shopFileOrNull)
                .orEmpty()
        }
    }

    override suspend fun fetchShop(shopFileName: String): ShopConfig {
        return withContext(limitedDispatcher) {
            DefaultSpigotFileManager(plugin, shopFileName).let(shopItemParser::parseShopFile)
        }
    }

    override suspend fun updateShop(shopConfig: ShopConfig) {
        return withContext(limitedDispatcher) {
            shopItemParser.saveItems(shopConfig)
        }
    }
}
