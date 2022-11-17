package ru.astrainteractive.astrashop.domain.interactors

import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.domain.IUseCase
import ru.astrainteractive.astrashop.domain.models.ShopConfig
import ru.astrainteractive.astrashop.domain.usecases.BuyUseCase
import ru.astrainteractive.astrashop.domain.usecases.ChangeStockAmountUseCase
import ru.astrainteractive.astrashop.domain.usecases.SellUseCase

class SellInteractor(
    private val sellUseCase: SellUseCase,
    private val changeStockAmountUseCase: ChangeStockAmountUseCase
) : IUseCase<Boolean, SellInteractor.Param> {
    class Param(
        val sellAmount: Int,
        val shopItem: ShopConfig.ShopItem,
        val shopConfig: ShopConfig,
        val player: Player
    )

    override suspend fun run(params: Param): Boolean {
        val buyResult = sellUseCase.invoke(
            SellUseCase.Param(
                amount = params.sellAmount,
                shopItem = params.shopItem,
                player = params.player
            )
        ) as? SellUseCase.Result.Success ?: return false

        changeStockAmountUseCase.invoke(
            ChangeStockAmountUseCase.Param(
                shopItem = params.shopItem,
                increaseAmount = buyResult.soldAmount,
                shopConfig = params.shopConfig
            )
        )
        return true
    }

}