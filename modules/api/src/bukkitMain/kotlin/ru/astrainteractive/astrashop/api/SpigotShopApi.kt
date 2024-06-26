package ru.astrainteractive.astrashop.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.plugin.Plugin
import ru.astrainteractive.astralibs.filemanager.DefaultFileConfigurationManager
import ru.astrainteractive.astralibs.filemanager.FileConfigurationManager
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.api.parser.ShopItemParserImpl
import ru.astrainteractive.astrashop.api.parser.util.getYmlFiles

internal class SpigotShopApi(
    private val plugin: Plugin,
    private val shopItemParser: ShopItemParserImpl
) : ShopApi {
    private val limitedDispatcher = Dispatchers.IO.limitedParallelism(1)

    private fun shopFileOrNull(fileManager: FileConfigurationManager): ShopConfig? {
        return runCatching { shopItemParser.parseShopFile(fileManager) }
            .onFailure(Throwable::printStackTrace)
            .getOrNull()
    }

    override suspend fun fetchShopList(): List<ShopConfig> {
        return withContext(limitedDispatcher) {
            getYmlFiles(plugin)
                .mapNotNull(::shopFileOrNull)
        }
    }

    override suspend fun fetchShop(shopFileName: String): ShopConfig {
        return withContext(limitedDispatcher) {
            DefaultFileConfigurationManager(plugin, shopFileName).let(shopItemParser::parseShopFile)
        }
    }

    override suspend fun updateShop(shopConfig: ShopConfig) {
        return withContext(limitedDispatcher) {
            shopItemParser.saveItems(shopConfig)
        }
    }
}
