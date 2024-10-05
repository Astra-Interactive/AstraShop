package ru.astrainteractive.astrashop.command.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astrashop.core.LifecyclePlugin
import ru.astrainteractive.astrashop.core.PluginTranslation
import ru.astrainteractive.astrashop.core.di.BukkitCoreModule
import ru.astrainteractive.astrashop.core.util.getValue
import ru.astrainteractive.astrashop.gui.router.GuiRouter
import ru.astrainteractive.astrashop.gui.router.di.RouterModule
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

interface CommandManagerDependencies {
    val plugin: LifecyclePlugin
    val translation: PluginTranslation
    val scope: CoroutineScope
    val dispatchers: KotlinDispatchers
    val kyoriComponentSerializer: KyoriComponentSerializer
    val router: GuiRouter

    class Default(
        coreModule: BukkitCoreModule,
        routerModule: RouterModule
    ) : CommandManagerDependencies {
        override val plugin: LifecyclePlugin = coreModule.plugin
        override val translation: PluginTranslation by coreModule.translation
        override val scope: CoroutineScope = coreModule.scope
        override val dispatchers: KotlinDispatchers = coreModule.dispatchers
        override val kyoriComponentSerializer: KyoriComponentSerializer by coreModule.kyoriComponentSerializer
        override val router: GuiRouter = routerModule.router
    }
}
