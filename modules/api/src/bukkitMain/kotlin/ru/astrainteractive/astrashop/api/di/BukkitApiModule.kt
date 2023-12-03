package ru.astrainteractive.astrashop.api.di

import org.bukkit.plugin.Plugin
import ru.astrainteractive.astrashop.api.ShopApi
import ru.astrainteractive.astrashop.api.SpigotShopApi

interface BukkitApiModule : PlatformApiModule {
    class Default(plugin: Plugin) : BukkitApiModule {
        override val shopApi: ShopApi by lazy {
            SpigotShopApi(plugin)
        }
    }
}
