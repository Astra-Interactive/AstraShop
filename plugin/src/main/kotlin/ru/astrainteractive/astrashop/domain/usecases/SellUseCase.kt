package ru.astrainteractive.astrashop.domain.usecases

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.domain.IUseCase
import ru.astrainteractive.astralibs.utils.economy.IEconomyProvider
import ru.astrainteractive.astrashop.asState
import ru.astrainteractive.astrashop.domain.models.ShopConfig
import ru.astrainteractive.astrashop.domain.models.ShopItemStack
import ru.astrainteractive.astrashop.domain.models.ShopMaterial
import ru.astrainteractive.astrashop.gui.buy.BuyState
import ru.astrainteractive.astrashop.utils.copy
import ru.astrainteractive.astrashop.utils.hasAtLeast
import ru.astrainteractive.astrashop.utils.toItemStack

class SellUseCase(private val economy: IEconomyProvider) : IUseCase<SellUseCase.Result, SellUseCase.Param> {

    class Param(
        val amount: Int,
        val shopItem: ShopConfig.ShopItem,
        val player: Player
    )

    sealed interface Result {
        object Failure : Result
        class Success(val soldAmount: Int) : Result
    }

    override suspend fun run(params: Param): Result {
        val item = params.shopItem
        val player = params.player
        val amount = params.amount
        if (item.getSellPrice() <= 0) {
            player.sendMessage("Предмет не закупается")
            return Result.Failure
        }
        val itemStack = when (item) {
            is ShopItemStack -> {
                if (!player.inventory.contains(item.itemStack)) {
                    player.sendMessage("У вас нет такого предмета")
                    return Result.Failure
                }
                item.itemStack
            }

            is ShopMaterial -> {
                if (!player.inventory.contains(item.material)) {
                    player.sendMessage("У вас нет такого предмета")
                    return Result.Failure
                }
                ItemStack(item.material)
            }

            else -> throw Exception("Not spigot item")
        }
        val couldNotRemoveAmount = player.inventory.removeItem(itemStack.copy(amount)).map { it.value.amount }.sum()
        val sellAmount = amount - couldNotRemoveAmount
        val money = sellAmount * item.getSellPrice()
        economy.addMoney(player.uniqueId, money)
        player.sendMessage("Вы получили $money\$")
        return Result.Success(sellAmount)
    }

}