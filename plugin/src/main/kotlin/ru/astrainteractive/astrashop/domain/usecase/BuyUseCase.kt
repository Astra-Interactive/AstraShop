package ru.astrainteractive.astrashop.domain.usecase

import kotlinx.coroutines.withContext
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astrashop.api.calculator.PriceCalculator
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.util.copy
import ru.astrainteractive.astrashop.util.hasAtLeast
import ru.astrainteractive.astrashop.util.toItemStack
import ru.astrainteractive.klibs.mikro.core.domain.UseCase

class BuyUseCase(
    private val economy: EconomyProvider,
    private val logger: Logger,
    private val dispatchers: BukkitDispatchers
) : UseCase.Suspended<BuyUseCase.Param, BuyUseCase.Result> {

    class Param(
        val amount: Int,
        val shopItem: ShopConfig.ShopItem,
        val player: Player
    )

    sealed interface Result {
        object Failure : Result
        class Success(val boughtAmount: Int) : Result
    }

    override suspend operator fun invoke(input: Param): Result {
        val item = input.shopItem
        val player = input.player
        val amount = input.amount

        val totalPrice = PriceCalculator.calculateBuyPrice(item, amount)
        if (totalPrice <= 0) {
            player.sendMessage("Предмет не продается")
            return Result.Failure
        }
        if (item.stock != -1 && item.stock <= 1) {
            player.sendMessage("В магазине недостаточно предметов")
            return Result.Failure
        }
        if (!economy.hasAtLeast(totalPrice, player.uniqueId)) {
            player.sendMessage("Недостаточно денег")
            return Result.Failure
        }
        if (!economy.takeMoney(player.uniqueId, totalPrice)) {
            player.sendMessage("Недостаточно денег")
            return Result.Failure
        }
        player.sendMessage("Вы потратили $totalPrice\$")
        val itemStack = item.toItemStack().copy(amount)
        val notFittedItems = player.inventory.addItem(itemStack)
        if (notFittedItems.isEmpty()) {
            logger.info(
                "BuyUseCase",
                "${player.name} bought $amount of ${itemStack.type.name} for $totalPrice",
                logInFile = true
            )
            return Result.Success(-amount)
        }
        player.sendMessage("Некоторые предметы не вместились. Они лежат на полу")
        withContext(dispatchers.BukkitMain) {
            player.location.world.dropItemNaturally(player.location, itemStack.copy(notFittedItems.size))
        }
        logger.info(
            "BuyUseCase",
            "${player.name} bought $amount of ${itemStack.type.name} for $totalPrice",
            logInFile = true
        )
        return Result.Success(-amount)
    }
}
