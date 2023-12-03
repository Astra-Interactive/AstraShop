package ru.astrainteractive.astrashop.api.di

import ru.astrainteractive.astrashop.api.ShopApi

interface ApiModule {
    val shopApi: ShopApi

    class Default(platformApiModule: PlatformApiModule) : ApiModule {
        override val shopApi: ShopApi = platformApiModule.shopApi
    }
}
