package ru.astrainteractive.astrashop.di.impl

import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astrashop.command.di.CommandContainer
import ru.astrainteractive.astrashop.di.RootModule
import ru.astrainteractive.astrashop.util.PluginTranslation
import ru.astrainteractive.klibs.kdi.Dependency
import ru.astrainteractive.klibs.kdi.getValue

class CommandContainerImpl(rootModule: RootModule) : CommandContainer {
    override val translation: Dependency<PluginTranslation> = rootModule.translation
    override val scope: Dependency<AsyncComponent> = rootModule.scope
    override val dispatchers: Dependency<BukkitDispatchers> = rootModule.dispatchers
}
