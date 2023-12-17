package ru.astrainteractive.astrashop.domain.usecase

import ru.astrainteractive.astrashop.api.ShopApi
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.klibs.mikro.core.domain.UseCase

class ChangeStockAmountUseCase(
    private val shopApi: ShopApi
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
        shopApi.updateShop(shopConfig)
    }
}
