package ru.astrainteractive.astrashop.di.impl

import ru.astrainteractive.astrashop.api.interactors.BuyInteractor
import ru.astrainteractive.astrashop.api.interactors.SellInteractor
import ru.astrainteractive.astrashop.api.usecases.BuyUseCase
import ru.astrainteractive.astrashop.api.usecases.ChangeStockAmountUseCase
import ru.astrainteractive.astrashop.api.usecases.SellUseCase
import ru.astrainteractive.astrashop.di.InteractorsFactoryModule
import ru.astrainteractive.klibs.kdi.Factory
import ru.astrainteractive.klibs.kdi.getValue

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
