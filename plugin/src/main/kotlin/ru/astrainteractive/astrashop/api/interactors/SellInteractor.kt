package ru.astrainteractive.astrashop.api.interactors

import org.bukkit.entity.Player
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.api.model.SpigotShopItem
import ru.astrainteractive.astrashop.api.model.SpigotTitleItem
import ru.astrainteractive.astrashop.api.usecases.ChangeStockAmountUseCase
import ru.astrainteractive.astrashop.api.usecases.SellUseCase
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
