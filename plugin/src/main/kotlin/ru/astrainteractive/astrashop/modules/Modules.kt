package ru.astrainteractive.astrashop.modules

import ru.astrainteractive.astralibs.di.factory
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.di.module
import ru.astrainteractive.astralibs.di.reloadable
import ru.astrainteractive.astralibs.utils.economy.EconomyProvider
import ru.astrainteractive.astralibs.utils.economy.VaultEconomyProvider
import ru.astrainteractive.astrashop.AstraShop
import ru.astrainteractive.astrashop.domain.SpigotShopApi
import ru.astrainteractive.astrashop.domain.interactors.BuyInteractor
import ru.astrainteractive.astrashop.domain.interactors.SellInteractor
import ru.astrainteractive.astrashop.domain.usecases.BuyUseCase
import ru.astrainteractive.astrashop.domain.usecases.ChangeStockAmountUseCase
import ru.astrainteractive.astrashop.domain.usecases.SellUseCase
import ru.astrainteractive.astrashop.utils.PluginTranslation


val DataSourceModule = module {
    SpigotShopApi(AstraShop.instance)
}
val TranslationModule = reloadable {
    PluginTranslation()
}
val EconomyModule = reloadable<EconomyProvider> {
//    MockEconomyProvider
    VaultEconomyProvider.also { VaultEconomyProvider.onEnable() }
}
val BuyInteractorModule = factory {
    val economy: EconomyProvider by EconomyModule
    BuyInteractor(
        BuyUseCase(economy),
        ChangeStockAmountUseCase(DataSourceModule.value)
    )
}
val SellInteractorModule = factory {
    val economy: EconomyProvider by EconomyModule
    SellInteractor(
        SellUseCase(economy),
        ChangeStockAmountUseCase(DataSourceModule.value)
    )
}