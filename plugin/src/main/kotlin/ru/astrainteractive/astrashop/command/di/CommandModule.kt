package ru.astrainteractive.astrashop.command.di

import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astrashop.util.PluginTranslation
import ru.astrainteractive.klibs.kdi.Dependency

interface CommandModule {
    val translation: Dependency<PluginTranslation>
    val scope: Dependency<AsyncComponent>
    val dispatchers: Dependency<BukkitDispatchers>
}
