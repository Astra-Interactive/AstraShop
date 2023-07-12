@file:OptIn(UnsafeApi::class)

package ru.astrainteractive.astrashop.di.impl

import org.jetbrains.kotlin.tooling.core.UnsafeApi
import ru.astrainteractive.astralibs.Lateinit
import ru.astrainteractive.astralibs.Reloadable
import ru.astrainteractive.astralibs.Single
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.async.DefaultBukkitDispatchers
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.economy.VaultEconomyProvider
import ru.astrainteractive.astralibs.filemanager.DefaultSpigotFileManager
import ru.astrainteractive.astralibs.getValue
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.utils.buildWithSpigot
import ru.astrainteractive.astrashop.AstraShop
import ru.astrainteractive.astrashop.di.RootModule
import ru.astrainteractive.astrashop.domain.SpigotShopApi
import ru.astrainteractive.astrashop.util.PluginTranslation

object RootModuleImpl : RootModule {
    override val plugin: Lateinit<AstraShop> = Lateinit()
    override val translation = Reloadable {
        val plugin by plugin
        val fileManager = DefaultSpigotFileManager(plugin, "translations.yml")
        PluginTranslation(fileManager)
    }
    override val spigotShopApi: Single<SpigotShopApi> = Single {
        val plugin by plugin
        SpigotShopApi(plugin)
    }
    override val economyProvider: Single<EconomyProvider> = Single {
        VaultEconomyProvider()
    }
    override val logger: Single<Logger> = Single {
        val plugin by plugin
        Logger.buildWithSpigot("AstraShop", plugin)
    }
    override val dispatchers: Single<BukkitDispatchers> = Single {
        val plugin by plugin
        DefaultBukkitDispatchers(plugin)
    }
    override val scope: Single<AsyncComponent> = Single {
        PluginScope
    }
}
