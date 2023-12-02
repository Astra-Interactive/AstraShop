package ru.astrainteractive.astrashop.di.impl

import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.async.DefaultBukkitDispatchers
import ru.astrainteractive.astralibs.economy.AnyEconomyProvider
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.filemanager.DefaultSpigotFileManager
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.menu.event.DefaultInventoryClickEvent
import ru.astrainteractive.astralibs.util.buildWithSpigot
import ru.astrainteractive.astrashop.AstraShop
import ru.astrainteractive.astrashop.di.RootModule
import ru.astrainteractive.astrashop.domain.SpigotShopApi
import ru.astrainteractive.astrashop.util.PluginTranslation
import ru.astrainteractive.klibs.kdi.Lateinit
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
        AsyncComponent.Default()
    }
    override val inventoryClickEvent: Single<EventListener> = Single {
        DefaultInventoryClickEvent()
    }
}
