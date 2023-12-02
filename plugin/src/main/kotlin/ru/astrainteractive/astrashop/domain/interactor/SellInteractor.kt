package ru.astrainteractive.astrashop.domain.interactor

import org.bukkit.entity.Player
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.domain.usecase.ChangeStockAmountUseCase
import ru.astrainteractive.astrashop.domain.usecase.SellUseCase
import ru.astrainteractive.klibs.mikro.core.domain.UseCase

class SellInteractor(
    private val sellUseCase: SellUseCase,
    private val changeStockAmountUseCase: ChangeStockAmountUseCase
) : UseCase.Suspended<SellInteractor.Param, Boolean> {
    class Param(
        val sellAmount: Int,
        val shopItem: ShopConfig.ShopItem,
        val shopConfig: ShopConfig,
        val player: Player
    )

    override suspend operator fun invoke(input: Param): Boolean {
        val buyResult = sellUseCase.invoke(
            SellUseCase.Param(
                amount = input.sellAmount,
                shopItem = input.shopItem,
                player = input.player
            )
        ) as? SellUseCase.Result.Success ?: return false

        changeStockAmountUseCase.invoke(
            ChangeStockAmountUseCase.Param(
                shopItem = input.shopItem,
                increaseAmount = buyResult.soldAmount,
                shopConfig = input.shopConfig
            )
        )
        return true
    }
}
