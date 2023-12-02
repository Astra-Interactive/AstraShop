package ru.astrainteractive.astrashop.domain.usecases

import ru.astrainteractive.astrashop.domain.ShopApi
import ru.astrainteractive.astrashop.domain.model.ShopConfig
import ru.astrainteractive.klibs.mikro.core.domain.UseCase

class ChangeStockAmountUseCase(
    private val dataSource: ShopApi
) : UseCase.Suspended<ChangeStockAmountUseCase.Param, Unit> {
    class Param(
        val shopItem: ShopConfig.ShopItem,
        val increaseAmount: Int,
        val shopConfig: ShopConfig
    )

    override suspend operator fun invoke(input: Param) {
        val item = input.shopItem
        val i = input.increaseAmount
        val shopConfig = input.shopConfig
        if (item.stock != -1) {
            val newAmount = (item.stock + i).coerceAtLeast(1)
            item.stock = newAmount
        }
        dataSource.updateShop(shopConfig)
    }
}
