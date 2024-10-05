package ru.astrainteractive.astrashop.api

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bukkit.plugin.Plugin
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.api.parser.ShopItemParserImpl
import ru.astrainteractive.astrashop.api.parser.util.getYmlFiles
import java.io.File

internal class SpigotShopApi(
    private val plugin: Plugin,
    private val shopItemParser: ShopItemParserImpl
) : ShopApi {
    private val mutex = Mutex()

    private fun shopFileOrNull(file: File): ShopConfig? {
        return runCatching { shopItemParser.parseShopFile(file) }
            .onFailure(Throwable::printStackTrace)
            .getOrNull()
    }

    override suspend fun fetchShopList(): List<ShopConfig> {
        return mutex.withLock {
            getYmlFiles(plugin).mapNotNull(::shopFileOrNull)
        }
    }

    override suspend fun fetchShop(shopFileName: String): ShopConfig {
        return mutex.withLock {
            shopItemParser.parseShopFile(
                plugin.dataFolder
                    .resolve("shops")
                    .resolve(shopFileName)
            )
        }
    }

    override suspend fun updateShop(shopConfig: ShopConfig) {
        return mutex.withLock {
            shopItemParser.saveItems(shopConfig)
        }
    }
}
