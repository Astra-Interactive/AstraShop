package ru.astrainteractive.astrashop.domain.di

import ru.astrainteractive.astrashop.di.RootModule
import ru.astrainteractive.astrashop.domain.interactor.BuyInteractor
import ru.astrainteractive.astrashop.domain.interactor.BuyInteractorImpl
import ru.astrainteractive.astrashop.domain.interactor.SellInteractor
import ru.astrainteractive.astrashop.domain.interactor.SellInteractorImpl
import ru.astrainteractive.astrashop.domain.usecase.BuyUseCaseImpl
import ru.astrainteractive.astrashop.domain.usecase.ChangeStockAmountUseCaseImpl
import ru.astrainteractive.astrashop.domain.usecase.SellUseCaseImpl
import ru.astrainteractive.klibs.kdi.Factory

interface DomainModule {
    val buyInteractor: Factory<BuyInteractor>
    val sellInteractor: Factory<SellInteractor>

    class Default(rootModule: RootModule) : DomainModule {
        override val buyInteractor: Factory<BuyInteractor> = Factory {
            BuyInteractorImpl(
                buyUseCase = BuyUseCaseImpl(
                    economy = rootModule.economyProvider.value,
                    logger = rootModule.logger.value,
                    dispatchers = rootModule.dispatchers.value
                ),
                changeStockAmountUseCase = ChangeStockAmountUseCaseImpl(
                    dataSource = rootModule.spigotShopApi.value
                )
            )
        }
        override val sellInteractor: Factory<SellInteractor> = Factory {
            SellInteractorImpl(
                sellUseCase = SellUseCaseImpl(
                    economy = rootModule.economyProvider.value,
                    logger = rootModule.logger.value,
                ),
                changeStockAmountUseCase = ChangeStockAmountUseCaseImpl(
                    dataSource = rootModule.spigotShopApi.value
                )
            )
        }
    }
}
