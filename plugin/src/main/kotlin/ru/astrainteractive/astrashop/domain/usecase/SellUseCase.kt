package ru.astrainteractive.astrashop.domain.usecase

import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.domain.bridge.PlayerBridge
import ru.astrainteractive.klibs.mikro.core.domain.UseCase
import java.util.UUID

class SellUseCase(
    private val economy: EconomyProvider,
    private val logger: Logger,
    private val playerBridge: PlayerBridge,
    private val calculatePriceUseCase: CalculatePriceUseCase
) : UseCase.Suspended<SellUseCase.Param, SellUseCase.Result> {

    class Param(
        val amount: Int,
        val shopItem: ShopConfig.ShopItem,
        val playerUUID: UUID
    )

    sealed interface Result {
        data object Failure : Result
        class Success(val soldAmount: Int) : Result
    }

    override suspend operator fun invoke(input: Param): Result {
        val item = input.shopItem
        val amount = input.amount
        val playerName = playerBridge.getName(input.playerUUID)

        // Is item purchasing
        val totalSellPrice = CalculatePriceUseCase.Input(
            item = item,
            amount = amount,
            type = CalculatePriceUseCase.Type.Sell
        ).let(calculatePriceUseCase::invoke).price
        if (totalSellPrice <= 0) {
            playerBridge.sendMessage(input.playerUUID, "Предмет не закупается".let(StringDesc::Raw))
            return Result.Failure
        }

        // Has item
        val couldNotRemoveAmount = playerBridge.removeItem(input.playerUUID, item, amount)
        if (couldNotRemoveAmount == null) {
            playerBridge.sendMessage(input.playerUUID, "У вас нет такого предмета".let(StringDesc::Raw))
            return Result.Failure
        }

        // Sell item
        val sellAmount = amount - couldNotRemoveAmount
        val money = CalculatePriceUseCase.Input(
            item = item,
            amount = sellAmount,
            type = CalculatePriceUseCase.Type.Sell
        ).let(calculatePriceUseCase::invoke).price
        economy.addMoney(input.playerUUID, money)
        playerBridge.sendMessage(input.playerUUID, "Вы получили $money\$".let(StringDesc::Raw))
        logger.info(
            "BuyUseCase",
            "$playerName sold $sellAmount of ${item.shopItem} for $money",
            logInFile = false
        )
        return Result.Success(sellAmount)
    }
}
