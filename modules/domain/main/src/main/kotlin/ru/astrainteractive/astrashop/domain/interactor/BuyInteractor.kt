package ru.astrainteractive.astrashop.domain.interactor

import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.domain.usecase.BuyUseCase
import ru.astrainteractive.astrashop.domain.usecase.ChangeStockAmountUseCase
import ru.astrainteractive.klibs.mikro.core.domain.UseCase
import java.util.UUID

class BuyInteractor(
    private val buyUseCase: BuyUseCase,
    private val changeStockAmountUseCase: ChangeStockAmountUseCase
) : UseCase.Suspended<BuyInteractor.Param, Boolean> {
    class Param(
        val buyAmount: Int,
        val shopItem: ShopConfig.ShopItem,
        val shopConfig: ShopConfig,
        val playerUUID: UUID
    )

    override suspend operator fun invoke(input: Param): Boolean {
        val buyResult = buyUseCase.invoke(
            BuyUseCase.Param(
                amount = input.buyAmount,
                shopItem = input.shopItem,
                playerUUID = input.playerUUID
            )
        ) as? BuyUseCase.Result.Success ?: return false

        changeStockAmountUseCase.invoke(
            ChangeStockAmountUseCase.Param(
                shopItem = input.shopItem,
                increaseAmount = buyResult.boughtAmount,
                shopConfig = input.shopConfig
            )
        )
        return true
    }
}
