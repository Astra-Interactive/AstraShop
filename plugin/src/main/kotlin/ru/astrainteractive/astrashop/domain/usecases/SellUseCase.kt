package ru.astrainteractive.astrashop.domain.usecases

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.domain.UseCase
import ru.astrainteractive.astralibs.utils.economy.EconomyProvider
import ru.astrainteractive.astrashop.domain.calculator.PriceCalculator
import ru.astrainteractive.astrashop.domain.models.ShopConfig
import ru.astrainteractive.astrashop.domain.models.SpigotShopItem
import ru.astrainteractive.astrashop.utils.copy

class SellUseCase(private val economy: EconomyProvider) : UseCase<SellUseCase.Result, SellUseCase.Param> {

    class Param(
        val amount: Int,
        val shopItem: ShopConfig.ShopItem<SpigotShopItem>,
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
        if (PriceCalculator.calculateSellPrice(item, amount) <= 0) {
            player.sendMessage("Предмет не закупается")
            return Result.Failure
        }
        val itemStack = when (val shopItem = item.shopItem) {
            is SpigotShopItem.ItemStack -> {
                if (!player.inventory.contains(shopItem.itemStack)) {
                    player.sendMessage("У вас нет такого предмета")
                    return Result.Failure
                }
                shopItem.itemStack
            }

            is SpigotShopItem.Material -> {
                if (!player.inventory.contains(shopItem.material)) {
                    player.sendMessage("У вас нет такого предмета")
                    return Result.Failure
                }
                ItemStack(shopItem.material)
            }

            else -> throw Exception("Not spigot item")
        }
        val couldNotRemoveAmount = player.inventory.removeItem(itemStack.copy(amount)).map { it.value.amount }.sum()
        val sellAmount = amount - couldNotRemoveAmount

        val money = PriceCalculator.calculateSellPrice(item, sellAmount)
        economy.addMoney(player.uniqueId, money)
        player.sendMessage("Вы получили $money\$")
        Logger.log(
            "BuyUseCase",
            "${player.name} sold ${sellAmount} of ${itemStack.type.name} for $money",
            consolePrint = false
        )
        return Result.Success(sellAmount)
    }

}