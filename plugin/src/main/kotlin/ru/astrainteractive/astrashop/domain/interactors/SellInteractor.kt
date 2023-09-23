package ru.astrainteractive.astrashop.domain.interactors

import org.bukkit.entity.Player
import ru.astrainteractive.astrashop.domain.models.ShopConfig
import ru.astrainteractive.astrashop.domain.models.SpigotShopItem
import ru.astrainteractive.astrashop.domain.models.SpigotTitleItem
import ru.astrainteractive.astrashop.domain.usecases.ChangeStockAmountUseCase
import ru.astrainteractive.astrashop.domain.usecases.SellUseCase
import ru.astrainteractive.klibs.mikro.core.domain.UseCase

class SellInteractor(
    private val sellUseCase: SellUseCase,
    private val changeStockAmountUseCase: ChangeStockAmountUseCase
) : UseCase.Parametrized<SellInteractor.Param, Boolean> {
    class Param(
        val sellAmount: Int,
        val shopItem: ShopConfig.ShopItem<SpigotShopItem>,
        val shopConfig: ShopConfig<SpigotTitleItem, SpigotShopItem>,
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
