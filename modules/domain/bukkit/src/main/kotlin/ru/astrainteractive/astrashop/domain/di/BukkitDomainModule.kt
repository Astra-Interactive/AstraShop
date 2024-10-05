package ru.astrainteractive.astrashop.domain.di

import ru.astrainteractive.astrashop.api.di.ApiModule
import ru.astrainteractive.astrashop.core.di.CoreModule
import ru.astrainteractive.astrashop.domain.bridge.BukkitPlayerBridge

class BukkitDomainModule(
    coreModule: CoreModule,
    apiModule: ApiModule,
) : DomainModule by DomainModule.Default(
    coreModule = coreModule,
    apiModule = apiModule,
    createPlayerBridge = {
        BukkitPlayerBridge(
            kyoriComponentSerializerKrate = coreModule.kyoriComponentSerializer,
            dispatchers = coreModule.dispatchers
        )
    }
)
