package ru.astrainteractive.astrashop.di.impl

import ru.astrainteractive.astralibs.Factory
import ru.astrainteractive.astralibs.getValue
import ru.astrainteractive.astrashop.di.InteractorsFactoryModule
import ru.astrainteractive.astrashop.domain.interactors.BuyInteractor
import ru.astrainteractive.astrashop.domain.interactors.SellInteractor
import ru.astrainteractive.astrashop.domain.usecases.BuyUseCase
import ru.astrainteractive.astrashop.domain.usecases.ChangeStockAmountUseCase
import ru.astrainteractive.astrashop.domain.usecases.SellUseCase

object InteractorsFactoryModuleImpl : InteractorsFactoryModule {
    private val rootModule by RootModuleImpl
    override val buyInteractor: Factory<BuyInteractor> = Factory {
        BuyInteractor(
            buyUseCase = BuyUseCase(
                economy = rootModule.economyProvider.value,
                logger = rootModule.logger.value,
                dispatchers = rootModule.dispatchers.value
            ),
            changeStockAmountUseCase = ChangeStockAmountUseCase(
                dataSource = rootModule.spigotShopApi.value
            )
        )
    }
    override val sellInteractor: Factory<SellInteractor> = Factory {
        SellInteractor(
            sellUseCase = SellUseCase(
                economy = rootModule.economyProvider.value,
                logger = rootModule.logger.value,
            ),
            changeStockAmountUseCase = ChangeStockAmountUseCase(
                dataSource = rootModule.spigotShopApi.value
            )
        )
    }
}
