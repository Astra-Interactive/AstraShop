package ru.astrainteractive.astrashop.command.di

import ru.astrainteractive.astralibs.Dependency
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astrashop.util.PluginTranslation

interface CommandModule {
    val translation: Dependency<PluginTranslation>
    val scope: Dependency<AsyncComponent>
    val dispatchers: Dependency<BukkitDispatchers>
}
