package ru.astrainteractive.astrashop.domain.usecases

import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.domain.IUseCase
import ru.astrainteractive.astralibs.utils.economy.IEconomyProvider
import ru.astrainteractive.astrashop.asState
import ru.astrainteractive.astrashop.domain.models.ShopConfig
import ru.astrainteractive.astrashop.gui.buy.BuyState
import ru.astrainteractive.astrashop.utils.copy
import ru.astrainteractive.astrashop.utils.hasAtLeast
import ru.astrainteractive.astrashop.utils.toItemStack

class BuyUseCase(private val economy: IEconomyProvider) : IUseCase<BuyUseCase.Result, BuyUseCase.Param> {

    class Param(
        val amount: Int,
        val shopItem: ShopConfig.ShopItem,
        val player: Player
    )

    sealed interface Result {
        object Failure : Result
        class Success(val boughtAmount: Int) : Result
    }

    override suspend fun run(params: Param): Result {
        val item = params.shopItem
        val player = params.player
        val amount = params.amount
        if (item.price <= 0) {
            player.sendMessage("Предмет не продается")
            return Result.Failure
        }
        if (item.stock != -1 && item.stock <= 1) {
            player.sendMessage("В магазине недостаточно предметов")
            return Result.Failure
        }
        if (!economy.hasAtLeast(item.price, player.uniqueId)) {
            player.sendMessage("Недостаточно денег")
            return Result.Failure
        }
        if (!economy.takeMoney(player.uniqueId, item.price)) {
            player.sendMessage("Недостаточно денег")
            return Result.Failure
        }
        val itemStack = item.toItemStack().copy(amount)
        val notFittedItems = player.inventory.addItem(itemStack)
        if (notFittedItems.isEmpty()) return Result.Success(-amount)
        player.sendMessage("Некоторые предметы не вместились. Они лежат на полу")
        player.location.world.dropItemNaturally(player.location, itemStack.copy(notFittedItems.size))
        return Result.Success(-amount)
    }

}