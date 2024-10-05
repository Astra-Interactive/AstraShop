package ru.astrainteractive.astrashop.di

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astrashop.api.di.ApiModule
import ru.astrainteractive.astrashop.api.di.BukkitApiModule
import ru.astrainteractive.astrashop.command.di.CommandsModule
import ru.astrainteractive.astrashop.core.LifecyclePlugin
import ru.astrainteractive.astrashop.core.di.BukkitCoreModule
import ru.astrainteractive.astrashop.domain.di.BukkitDomainModule
import ru.astrainteractive.astrashop.domain.di.DomainModule
import ru.astrainteractive.astrashop.gui.router.di.BukkitRouterModule
import ru.astrainteractive.astrashop.gui.router.di.RouterModule

interface RootModule {
    val lifecycle: Lifecycle

    val coreModule: BukkitCoreModule
    val apiModule: ApiModule
    val domainModule: DomainModule
    val routerModule: RouterModule
    val commandsModule: CommandsModule

    class Default(plugin: LifecyclePlugin) : RootModule {
        override val coreModule by lazy {
            BukkitCoreModule.Default(plugin)
        }

        override val apiModule: ApiModule = BukkitApiModule.Default(coreModule.plugin)

        override val domainModule: DomainModule = BukkitDomainModule(
            coreModule = coreModule,
            apiModule = apiModule,
        )

        override val routerModule: RouterModule by lazy {
            BukkitRouterModule(
                coreModule = coreModule,
                apiModule = apiModule,
                domainModule = domainModule
            )
        }

        override val commandsModule: CommandsModule by lazy {
            CommandsModule.Default(
                coreModule,
                routerModule
            )
        }

        private val lifecycles: List<Lifecycle>
            get() = listOf(
                coreModule.lifecycle
            )

        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onEnable = {
                lifecycles.forEach(Lifecycle::onEnable)
            },
            onReload = {
                lifecycles.forEach(Lifecycle::onReload)
            },
            onDisable = {
                lifecycles.forEach(Lifecycle::onDisable)
                HandlerList.unregisterAll(coreModule.plugin)
                Bukkit.getOnlinePlayers().forEach(Player::closeInventory)
            }
        )
    }
}
