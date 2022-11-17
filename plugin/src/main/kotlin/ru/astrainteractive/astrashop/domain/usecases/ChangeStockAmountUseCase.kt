package ru.astrainteractive.astrashop.domain.usecases

import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.domain.IUseCase
import ru.astrainteractive.astralibs.utils.economy.IEconomyProvider
import ru.astrainteractive.astrashop.asState
import ru.astrainteractive.astrashop.domain.IDataSource
import ru.astrainteractive.astrashop.domain.SpigotDataSource
import ru.astrainteractive.astrashop.domain.models.ShopConfig
import ru.astrainteractive.astrashop.gui.buy.BuyState
import ru.astrainteractive.astrashop.utils.copy
import ru.astrainteractive.astrashop.utils.hasAtLeast
import ru.astrainteractive.astrashop.utils.toItemStack

class ChangeStockAmountUseCase(private val dataSource: IDataSource) : IUseCase<Unit, ChangeStockAmountUseCase.Param> {
    class Param(
        val shopItem: ShopConfig.ShopItem,
        val increaseAmount: Int,
        val shopConfig: ShopConfig
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