package ru.astrainteractive.astrashop.domain.interactors

import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.domain.IUseCase
import ru.astrainteractive.astrashop.domain.models.ShopConfig
import ru.astrainteractive.astrashop.domain.usecases.BuyUseCase
import ru.astrainteractive.astrashop.domain.usecases.ChangeStockAmountUseCase

class BuyInteractor(
    private val buyUseCase: BuyUseCase,
    private val changeStockAmountUseCase: ChangeStockAmountUseCase
) : IUseCase<Boolean, BuyInteractor.Param> {
    class Param(
        val buyAmount: Int,
        val shopItem: ShopConfig.ShopItem,
        val shopConfig: ShopConfig,
        val player: Player
    )

    override suspend fun run(params: Param): Boolean {
        val buyResult = buyUseCase.invoke(
            BuyUseCase.Param(
                amount = params.buyAmount,
                shopItem = params.shopItem,
                player = params.player
            )
        ) as? BuyUseCase.Result.Success ?: return false

        changeStockAmountUseCase.invoke(
            ChangeStockAmountUseCase.Param(
                shopItem = params.shopItem,
                increaseAmount = buyResult.boughtAmount,
                shopConfig = params.shopConfig
            )
        )
        return true
    }

}