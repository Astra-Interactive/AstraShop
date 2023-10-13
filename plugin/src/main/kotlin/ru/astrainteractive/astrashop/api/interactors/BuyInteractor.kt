package ru.astrainteractive.astrashop.api.interactors

import org.bukkit.entity.Player
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.api.model.SpigotShopItem
import ru.astrainteractive.astrashop.api.model.SpigotTitleItem
import ru.astrainteractive.astrashop.api.usecases.BuyUseCase
import ru.astrainteractive.astrashop.api.usecases.ChangeStockAmountUseCase
import ru.astrainteractive.klibs.mikro.core.domain.UseCase

class BuyInteractor(
    private val buyUseCase: BuyUseCase,
    private val changeStockAmountUseCase: ChangeStockAmountUseCase
) : UseCase.Parametrized<BuyInteractor.Param, Boolean> {
    class Param(
        val buyAmount: Int,
        val shopItem: ShopConfig.ShopItem<SpigotShopItem>,
        val shopConfig: ShopConfig<SpigotTitleItem, SpigotShopItem>,
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
