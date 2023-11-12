@file:OptIn(UnsafeApi::class)

package ru.astrainteractive.astrashop.di.impl

import org.jetbrains.kotlin.tooling.core.UnsafeApi
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.async.DefaultBukkitDispatchers
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.economy.AnyEconomyProvider
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.filemanager.DefaultSpigotFileManager
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.util.buildWithSpigot
import ru.astrainteractive.astrashop.AstraShop
import ru.astrainteractive.astrashop.api.impl.SpigotShopApi
import ru.astrainteractive.astrashop.di.RootModule
import ru.astrainteractive.astrashop.domain.di.DomainModule
import ru.astrainteractive.astrashop.gui.router.GuiRouter
import ru.astrainteractive.astrashop.gui.router.Router
import ru.astrainteractive.astrashop.util.PluginTranslation
import ru.astrainteractive.klibs.kdi.Lateinit
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

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
        AnyEconomyProvider(plugin.value)
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

    override val router: Single<Router> = Single {
        GuiRouter(scope = scope.value, dispatchers = dispatchers.value)
    }

    override val domainModule: DomainModule by Provider {
        DomainModule.Default(this)
    }
}
