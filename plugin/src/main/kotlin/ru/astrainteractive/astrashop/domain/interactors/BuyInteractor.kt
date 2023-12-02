package ru.astrainteractive.astrashop.domain.interactors

import org.bukkit.entity.Player
import ru.astrainteractive.astrashop.domain.model.ShopConfig
import ru.astrainteractive.astrashop.domain.usecases.BuyUseCase
import ru.astrainteractive.astrashop.domain.usecases.ChangeStockAmountUseCase
import ru.astrainteractive.klibs.mikro.core.domain.UseCase

class BuyInteractor(
    private val buyUseCase: BuyUseCase,
    private val changeStockAmountUseCase: ChangeStockAmountUseCase
) : UseCase.Suspended<BuyInteractor.Param, Boolean> {
    class Param(
        val buyAmount: Int,
        val shopItem: ShopConfig.ShopItem,
        val shopConfig: ShopConfig,
        val player: Player
    )

    override suspend operator fun invoke(input: Param): Boolean {
        val buyResult = buyUseCase.invoke(
            BuyUseCase.Param(
                amount = input.buyAmount,
                shopItem = input.shopItem,
                player = input.player
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
