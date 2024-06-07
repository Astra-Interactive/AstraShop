package ru.astrainteractive.astrashop.di

import ru.astrainteractive.astrashop.api.di.ApiModule
import ru.astrainteractive.astrashop.api.di.BukkitApiModule
import ru.astrainteractive.astrashop.core.di.CoreModule
import ru.astrainteractive.astrashop.domain.di.DomainModule
import ru.astrainteractive.astrashop.gui.router.di.RouterModule

interface RootModule {
    val coreModule: CoreModule
    val apiModule: ApiModule
    val domainModule: DomainModule
    val routerModule: RouterModule

    class Default : RootModule {
        override val coreModule: CoreModule by lazy {
            CoreModule.Default()
        }
        override val apiModule: ApiModule by lazy {
            val bukkitApiModule = BukkitApiModule.Default(coreModule.plugin.value)
            ApiModule.Default(bukkitApiModule)
        }
        override val domainModule: DomainModule by lazy {
            DomainModule.Default(
                coreModule = coreModule,
                apiModule = apiModule
            )
        }
        override val routerModule: RouterModule by lazy {
            RouterModule.Default(this)
        }
    }
}
