package ru.astrainteractive.astrashop.modules

import ru.astrainteractive.astralibs.di.module
import ru.astrainteractive.astralibs.di.reloadable
import ru.astrainteractive.astralibs.di.value
import ru.astrainteractive.astralibs.utils.economy.IEconomyProvider
import ru.astrainteractive.astralibs.utils.economy.VaultEconomyProvider
import ru.astrainteractive.astrashop.domain.SpigotDataSource
import ru.astrainteractive.astrashop.domain.interactors.BuyInteractor
import ru.astrainteractive.astrashop.domain.interactors.SellInteractor
import ru.astrainteractive.astrashop.domain.usecases.BuyUseCase
import ru.astrainteractive.astrashop.domain.usecases.ChangeStockAmountUseCase
import ru.astrainteractive.astrashop.domain.usecases.SellUseCase
import ru.astrainteractive.astrashop.utils.MockEconomyProvider
import ru.astrainteractive.astrashop.utils.PluginTranslation
import java.util.*


val DataSourceModule = module {
    SpigotDataSource()
}
val TranslationModule = reloadable {
    PluginTranslation()
}
val EconomyModule = reloadable<IEconomyProvider> {
    MockEconomyProvider
//    VaultEconomyProvider.also { VaultEconomyProvider.onEnable() }
}
val BuyInteractorModule = value {
    BuyInteractor(
        BuyUseCase(VaultEconomyProvider),
        ChangeStockAmountUseCase(DataSourceModule.value)
    )
}
val SellInteractorModule = value {
    SellInteractor(
        SellUseCase(VaultEconomyProvider),
        ChangeStockAmountUseCase(DataSourceModule.value)
    )
}