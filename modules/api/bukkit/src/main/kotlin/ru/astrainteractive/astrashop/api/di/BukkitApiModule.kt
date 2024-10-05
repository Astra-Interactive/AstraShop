package ru.astrainteractive.astrashop.api.di

import org.bukkit.plugin.Plugin
import ru.astrainteractive.astrashop.api.ShopApi
import ru.astrainteractive.astrashop.api.SpigotShopApi
import ru.astrainteractive.astrashop.api.parser.ShopItemParserImpl

interface BukkitApiModule : ApiModule {
    class Default(plugin: Plugin) : BukkitApiModule {
        override val shopApi: ShopApi by lazy {
            val shopItemParser = ShopItemParserImpl(plugin)
            SpigotShopApi(plugin, shopItemParser)
        }
    }
}
