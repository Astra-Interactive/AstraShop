package ru.astrainteractive.astrashop.command.work.di

import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astrashop.api.di.ApiModule
import ru.astrainteractive.astrashop.command.work.ShopWorkerLifecycle

interface WorkerModule {
    val lifecycle: Lifecycle

    class Default(
        apiModule: ApiModule
    ) : WorkerModule {
        override val lifecycle = ShopWorkerLifecycle(apiModule.shopApi)
    }
}
