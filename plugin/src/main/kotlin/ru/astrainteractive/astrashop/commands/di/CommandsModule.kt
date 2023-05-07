package ru.astrainteractive.astrashop.commands.di

import ru.astrainteractive.astralibs.Dependency
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astrashop.utils.PluginTranslation

interface CommandsModule {
    val translation: Dependency<PluginTranslation>
    val scope: Dependency<AsyncComponent>
    val dispatchers: Dependency<BukkitDispatchers>
}
