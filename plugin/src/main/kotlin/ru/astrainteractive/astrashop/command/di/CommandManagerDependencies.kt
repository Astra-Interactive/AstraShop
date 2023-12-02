package ru.astrainteractive.astrashop.command.di

import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astrashop.AstraShop
import ru.astrainteractive.astrashop.core.PluginTranslation
import ru.astrainteractive.astrashop.core.di.CoreModule
import ru.astrainteractive.klibs.kdi.getValue

interface CommandManagerDependencies {
    val plugin: AstraShop
    val translation: PluginTranslation
    val scope: AsyncComponent
    val dispatchers: BukkitDispatchers

    class Default(coreModule: CoreModule) : CommandManagerDependencies {
        override val plugin: AstraShop by coreModule.plugin
        override val translation: PluginTranslation by coreModule.translation
        override val scope: AsyncComponent by coreModule.scope
        override val dispatchers: BukkitDispatchers by coreModule.dispatchers
    }
}
