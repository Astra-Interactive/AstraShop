package ru.astrainteractive.astrashop.command.di

import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astrashop.AstraShop
import ru.astrainteractive.astrashop.core.PluginTranslation
import ru.astrainteractive.astrashop.core.di.BukkitCoreModule
import ru.astrainteractive.astrashop.gui.router.GuiRouter
import ru.astrainteractive.astrashop.gui.router.di.RouterModule
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

interface CommandManagerDependencies {
    val plugin: AstraShop
    val translation: PluginTranslation
    val scope: AsyncComponent
    val dispatchers: KotlinDispatchers
    val kyoriComponentSerializer: KyoriComponentSerializer
    val router: GuiRouter

    class Default(coreModule: BukkitCoreModule, routerModule: RouterModule) : CommandManagerDependencies {
        override val plugin: AstraShop by Provider {
            coreModule.plugin.value as AstraShop
        }
        override val translation: PluginTranslation by coreModule.translation
        override val scope: AsyncComponent = coreModule.scope
        override val dispatchers: KotlinDispatchers = coreModule.dispatchers
        override val kyoriComponentSerializer: KyoriComponentSerializer by coreModule.kyoriComponentSerializer
        override val router: GuiRouter by Provider {
            routerModule.router
        }
    }
}
