package ru.astrainteractive.astrashop.command.di

import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astrashop.command.CommandManager
import ru.astrainteractive.astrashop.core.di.BukkitCoreModule
import ru.astrainteractive.astrashop.gui.router.di.RouterModule

interface CommandsModule {
    val lifecycle: Lifecycle

    class Default(
        coreModule: BukkitCoreModule,
        routerModule: RouterModule
    ) : CommandsModule {
        private val commandManager = CommandManager(
            module = CommandManagerDependencies.Default(
                coreModule = coreModule,
                routerModule = routerModule
            )
        )

        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onEnable = {
                commandManager.create()
            }
        )
    }
}
