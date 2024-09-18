package ru.astrainteractive.astrashop.gui.router.di

import ru.astrainteractive.astrashop.api.di.ApiModule
import ru.astrainteractive.astrashop.core.di.CoreModule
import ru.astrainteractive.astrashop.domain.di.DomainModule
import ru.astrainteractive.astrashop.gui.router.BukkitGuiRouter
import ru.astrainteractive.astrashop.gui.router.GuiRouter

class BukkitRouterModule(
    coreModule: CoreModule,
    apiModule: ApiModule,
    domainModule: DomainModule
) : RouterModule {
    private val guiModule: GuiModule = GuiModule.Default(
        coreModule = coreModule,
        apiModule = apiModule,
        domainModule = domainModule
    )

    override val router: GuiRouter = BukkitGuiRouter(
        coreModule = coreModule,
        guiModule = guiModule
    )
}
