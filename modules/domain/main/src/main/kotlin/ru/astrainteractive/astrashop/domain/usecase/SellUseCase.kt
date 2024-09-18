package ru.astrainteractive.astrashop.domain.usecase

import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.core.PluginTranslation
import ru.astrainteractive.astrashop.core.di.factory.CurrencyEconomyProviderFactory
import ru.astrainteractive.astrashop.domain.bridge.PlayerBridge
import ru.astrainteractive.klibs.mikro.core.domain.UseCase
import java.util.UUID
import ru.astrainteractive.astrashop.domain.calculator.PriceCalculator

class SellUseCase(
    private val currencyEconomyProviderFactory: CurrencyEconomyProviderFactory,
    private val playerBridge: PlayerBridge,
    private val translation: PluginTranslation
) : UseCase.Suspended<SellUseCase.Param, SellUseCase.Result>,
    Logger by JUtiltLogger("SellUseCase") {

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
        val economy = when (val currencyId = input.shopItem.buyCurrencyId) {
            null -> currencyEconomyProviderFactory.findDefault()
            else -> currencyEconomyProviderFactory.findByCurrencyId(currencyId)
        }
        if (economy == null) {
            error { "#invoke could not find currency with id ${input.shopItem.buyCurrencyId} (or default currency)" }
            return Result.Failure
        }
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
