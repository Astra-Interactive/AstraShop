package ru.astrainteractive.astrashop.gui.router.di

import ru.astrainteractive.astrashop.di.RootModule
import ru.astrainteractive.astrashop.gui.router.GuiRouter
import ru.astrainteractive.astrashop.gui.router.GuiRouterImpl
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

interface RouterModule {
    val router: GuiRouter

    class Default(rootModule: RootModule) : RouterModule {
        override val router: GuiRouter by Provider {
            GuiRouterImpl(
                coreModule = rootModule.coreModule,
                apiModule = rootModule.apiModule,
                domainModule = rootModule.domainModule
            )
        }
    }
}
