package ru.astrainteractive.astrashop.api.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.plugin.Plugin
import ru.astrainteractive.astralibs.filemanager.DefaultSpigotFileManager
import ru.astrainteractive.astrashop.api.ShopApi
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.api.model.SpigotShopItem
import ru.astrainteractive.astrashop.api.model.SpigotTitleItem
import ru.astrainteractive.astrashop.api.util.ShopItemParser
import ru.astrainteractive.astrashop.api.util.getYmlFiles

class SpigotShopApi(private val plugin: Plugin) : ShopApi<SpigotTitleItem, SpigotShopItem> {
    private val shopItemParser = ShopItemParser(plugin)
    private val readerDispatcher = Dispatchers.IO.limitedParallelism(1)

    override suspend fun fetchShopList(): List<ShopConfig<SpigotTitleItem, SpigotShopItem>> = withContext(
        readerDispatcher
    ) {
        getYmlFiles(plugin)?.mapNotNull(shopItemParser::parseShopFileOrNull) ?: emptyList()
    }

    override suspend fun fetchShop(configName: String): ShopConfig<SpigotTitleItem, SpigotShopItem> = withContext(
        readerDispatcher
    ) {
        DefaultSpigotFileManager(plugin, configName).let(shopItemParser::parseShopFile)
    }

    override suspend fun updateShop(shopConfig: ShopConfig<SpigotTitleItem, SpigotShopItem>) = withContext(
        readerDispatcher
    ) {
        shopItemParser.saveItem(shopConfig)
    }
}
