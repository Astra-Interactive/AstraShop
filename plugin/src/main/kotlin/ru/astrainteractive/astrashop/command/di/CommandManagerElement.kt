package ru.astrainteractive.astrashop.command.di

import ru.astrainteractive.astrashop.command.CommandManager
import ru.astrainteractive.astrashop.core.di.CoreModule
import ru.astrainteractive.astrashop.gui.router.di.RouterModule

interface CommandManagerElement {

    fun onEnable()

    class Default(
        private val coreModule: CoreModule,
        private val routerModule: RouterModule
    ) : CommandManagerElement {
        override fun onEnable() {
            val dependencies = CommandManagerDependencies.Default(coreModule, routerModule)
            CommandManager(dependencies).create()
        }
    }
}
