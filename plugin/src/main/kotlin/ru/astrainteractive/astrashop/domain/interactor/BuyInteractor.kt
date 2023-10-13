package ru.astrainteractive.astrashop.domain.interactor

import org.bukkit.entity.Player
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.api.model.SpigotShopItem
import ru.astrainteractive.astrashop.api.model.SpigotTitleItem
import ru.astrainteractive.astrashop.domain.interactor.BuyInteractor.Param
import ru.astrainteractive.astrashop.domain.usecase.BuyUseCase
import ru.astrainteractive.astrashop.domain.usecase.BuyUseCaseImpl
import ru.astrainteractive.astrashop.domain.usecase.ChangeStockAmountUseCase
import ru.astrainteractive.klibs.mikro.core.domain.UseCase

interface BuyInteractor : UseCase.Parametrized<Param, Boolean> {
    class Param(
        val buyAmount: Int,
        val shopItem: ShopConfig.ShopItem<SpigotShopItem>,
        val shopConfig: ShopConfig<SpigotTitleItem, SpigotShopItem>,
        val player: Player
    )
}

class BuyInteractorImpl(
    private val buyUseCase: BuyUseCaseImpl,
    private val changeStockAmountUseCase: ChangeStockAmountUseCase
) : BuyInteractor {

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
