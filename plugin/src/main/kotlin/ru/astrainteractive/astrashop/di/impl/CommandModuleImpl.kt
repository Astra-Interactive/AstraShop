package ru.astrainteractive.astrashop.di.impl

import ru.astrainteractive.astralibs.Dependency
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.getValue
import ru.astrainteractive.astrashop.command.di.CommandModule
import ru.astrainteractive.astrashop.util.PluginTranslation

object CommandModuleImpl : CommandModule {
    private val rootModule by RootModuleImpl
    override val translation: Dependency<PluginTranslation> = rootModule.translation
    override val scope: Dependency<AsyncComponent> = rootModule.scope
    override val dispatchers: Dependency<BukkitDispatchers> = rootModule.dispatchers
}
