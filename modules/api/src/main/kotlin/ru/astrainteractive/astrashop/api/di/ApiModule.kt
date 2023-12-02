package ru.astrainteractive.astrashop.api.di

import ru.astrainteractive.astrashop.api.ShopApi
import ru.astrainteractive.astrashop.api.calculator.PriceCalculator

interface ApiModule {
    val priceCalculator: PriceCalculator
    val shopApi: ShopApi

    class Default(platformApiModule: PlatformApiModule) : ApiModule {
        override val priceCalculator: PriceCalculator = PriceCalculator
        override val shopApi: ShopApi = platformApiModule.shopApi
    }
}
