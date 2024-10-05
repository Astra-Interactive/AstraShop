package ru.astrainteractive.astrashop.domain.di

import ru.astrainteractive.astrashop.api.di.ApiModule
import ru.astrainteractive.astrashop.core.di.CoreModule
import ru.astrainteractive.astrashop.domain.bridge.PlayerBridge
import ru.astrainteractive.astrashop.domain.interactor.BuyInteractor
import ru.astrainteractive.astrashop.domain.interactor.SellInteractor
import ru.astrainteractive.astrashop.domain.usecase.BuyUseCase
import ru.astrainteractive.astrashop.domain.usecase.ChangeStockAmountUseCase
import ru.astrainteractive.astrashop.domain.usecase.SellUseCase
import ru.astrainteractive.klibs.kdi.getValue

interface DomainModule {
    // UseCase
    val buyUseCase: BuyUseCase
    val changeStockAmountUseCase: ChangeStockAmountUseCase
    val sellUseCase: SellUseCase

    // Interactor
    val buyInteractor: BuyInteractor
    val sellInteractor: SellInteractor

    val playerBridge: PlayerBridge

    class Default(
        coreModule: CoreModule,
        apiModule: ApiModule,
        createPlayerBridge: () -> PlayerBridge
    ) : DomainModule {
        override val playerBridge: PlayerBridge = createPlayerBridge.invoke()

        override val buyUseCase = BuyUseCase(
            currencyEconomyProviderFactory = coreModule.currencyEconomyProviderFactory,
            playerBridge = playerBridge,
            translation = coreModule.translation.value
        )

        override val changeStockAmountUseCase = ChangeStockAmountUseCase(
            shopApi = apiModule.shopApi
        )

        override val sellUseCase = SellUseCase(
            currencyEconomyProviderFactory = coreModule.currencyEconomyProviderFactory,
            playerBridge = playerBridge,
            translation = coreModule.translation.value
        )

        override val buyInteractor = BuyInteractor(
            buyUseCase = buyUseCase,
            changeStockAmountUseCase = changeStockAmountUseCase,
        )

        override val sellInteractor = SellInteractor(
            sellUseCase = sellUseCase,
            changeStockAmountUseCase = changeStockAmountUseCase
        )
    }
}
