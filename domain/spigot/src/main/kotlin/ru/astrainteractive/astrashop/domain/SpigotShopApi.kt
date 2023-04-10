package ru.astrainteractive.astrashop.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.plugin.Plugin
import ru.astrainteractive.astralibs.file_manager.FileManager
import ru.astrainteractive.astrashop.domain.models.ShopConfig
import ru.astrainteractive.astrashop.domain.models.SpigotShopItem
import ru.astrainteractive.astrashop.domain.models.SpigotTitleItem
import ru.astrainteractive.astrashop.domain.utils.ShopItemParser
import ru.astrainteractive.astrashop.domain.utils.getYmlFiles

class SpigotShopApi(private val plugin: Plugin) : ShopApi<SpigotTitleItem,SpigotShopItem> {
    private val shopItemParser = ShopItemParser(plugin)
    private val readerDispatcher = Dispatchers.IO.limitedParallelism(1)

    override suspend fun fetchShopList(): List<ShopConfig<SpigotTitleItem,SpigotShopItem>> = withContext(readerDispatcher) {
        getYmlFiles(plugin)?.mapNotNull(shopItemParser::parseShopFileOrNull) ?: emptyList()
    }

    override suspend fun fetchShop(configName: String): ShopConfig<SpigotTitleItem,SpigotShopItem> = withContext(readerDispatcher) {
        FileManager(configName).let(shopItemParser::parseShopFile)
    }

    override suspend fun updateShop(shopConfig: ShopConfig<SpigotTitleItem,SpigotShopItem>) = withContext(readerDispatcher) {
        shopItemParser.saveItem(shopConfig)
    }
}