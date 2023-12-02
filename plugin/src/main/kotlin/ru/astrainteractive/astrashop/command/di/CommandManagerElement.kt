package ru.astrainteractive.astrashop.command.di

import ru.astrainteractive.astrashop.command.CommandManager
import ru.astrainteractive.astrashop.core.di.CoreModule

interface CommandManagerElement {

    fun onEnable()

    class Default(private val coreModule: CoreModule) : CommandManagerElement {
        override fun onEnable() {
            val dependencies = CommandManagerDependencies.Default(coreModule)
            CommandManager(dependencies, coreModule.translationContext).create()
        }
    }
}
