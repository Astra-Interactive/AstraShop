package ru.astrainteractive.astrashop.domain.usecases

import ru.astrainteractive.astralibs.domain.UseCase
import ru.astrainteractive.astrashop.domain.ShopApi
import ru.astrainteractive.astrashop.domain.models.ShopConfig
import ru.astrainteractive.astrashop.domain.models.SpigotShopItem
import ru.astrainteractive.astrashop.domain.models.SpigotTitleItem

class ChangeStockAmountUseCase(
    private val dataSource: ShopApi<SpigotTitleItem, SpigotShopItem>
) : UseCase<Unit, ChangeStockAmountUseCase.Param> {
    class Param(
        val shopItem: ShopConfig.ShopItem<SpigotShopItem>,
        val increaseAmount: Int,
        val shopConfig: ShopConfig<SpigotTitleItem, SpigotShopItem>
    )

    override suspend fun run(params: Param) {
        val item = params.shopItem
        val i = params.increaseAmount
        val shopConfig = params.shopConfig
        if (item.stock != -1) {
            val newAmount = (item.stock + i).coerceAtLeast(1)
            item.stock = newAmount
        }
        dataSource.updateShop(shopConfig)
    }
}
