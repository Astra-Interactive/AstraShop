package ru.astrainteractive.astrashop.domain.usecase

import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.logging.BukkitLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.core.PluginTranslation
import ru.astrainteractive.astrashop.domain.bridge.PlayerBridge
import ru.astrainteractive.klibs.mikro.core.domain.UseCase
import java.util.UUID

class SellUseCase(
    private val economy: EconomyProvider,
    private val playerBridge: PlayerBridge,
    private val translation: PluginTranslation
) : UseCase.Suspended<SellUseCase.Param, SellUseCase.Result>,
    Logger by BukkitLogger("SellUseCase") {

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
        val totalSellPrice = PriceCalculator.calculateSellPrice(item, amount)
        if (totalSellPrice <= 0) {
            playerBridge.sendMessage(input.playerUUID, translation.shop.itemNotForSelling)
            return Result.Failure
        }

        // Has item
        val couldNotRemoveAmount = playerBridge.removeItem(input.playerUUID, item, amount)
        if (couldNotRemoveAmount == null) {
            playerBridge.sendMessage(input.playerUUID, translation.shop.playerNotHaveItem)
            return Result.Failure
        }

        // Sell item
        val sellAmount = amount - couldNotRemoveAmount
        val money = PriceCalculator.calculateSellPrice(item, sellAmount)
        economy.addMoney(input.playerUUID, money)
        playerBridge.sendMessage(input.playerUUID, translation.shop.youEarnedAmount(money))
        info { "$playerName sold $sellAmount of ${item.shopItem} for $money" }
        return Result.Success(sellAmount)
    }
}
