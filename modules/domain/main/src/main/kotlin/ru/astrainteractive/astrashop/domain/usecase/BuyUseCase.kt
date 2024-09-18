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

class BuyUseCase(
    private val currencyEconomyProviderFactory: CurrencyEconomyProviderFactory,
    private val playerBridge: PlayerBridge,
    private val translation: PluginTranslation
) : UseCase.Suspended<BuyUseCase.Param, BuyUseCase.Result>,
    Logger by JUtiltLogger("BuyUseCase") {

    class Param(
        val amount: Int,
        val shopItem: ShopConfig.ShopItem,
        val playerUUID: UUID
    )

    sealed interface Result {
        data object Failure : Result
        class Success(val boughtAmount: Int) : Result
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
        // Is item for sale

        val totalPrice = PriceCalculator.calculateBuyPrice(item, amount)
        if (totalPrice <= 0) {
            playerBridge.sendMessage(input.playerUUID, translation.shop.itemNotForPurchase)
            return Result.Failure
        }
        // Is item exists in shop
        if (item.stock != -1 && item.stock <= 0 && !item.isPurchaseInfinite) {
            playerBridge.sendMessage(input.playerUUID, translation.shop.notEnoughItems)
            return Result.Failure
        }
        // Is player has money
        if (!economy.hasAtLeast(input.playerUUID, totalPrice)) {
            playerBridge.sendMessage(input.playerUUID, translation.shop.notEnoughMoney)
            return Result.Failure
        }

        if (!economy.takeMoney(input.playerUUID, totalPrice)) {
            playerBridge.sendMessage(input.playerUUID, translation.shop.notEnoughMoney)
            return Result.Failure
        }
        playerBridge.sendMessage(input.playerUUID, translation.shop.youSpentAmount(totalPrice))

        // Buy item
        val notFittedAmount = playerBridge.giveOrDropItems(input.playerUUID, item, amount)
        if (notFittedAmount == 0) {
            info { "$playerName bought $amount of ${item.shopItem} for $totalPrice" }
            return Result.Success(-amount)
        }
        playerBridge.sendMessage(
            input.playerUUID,
            translation.shop.notFitted
        )
        info { "$playerName bought $amount of ${item.shopItem} for $totalPrice" }
        return Result.Success(-amount)
    }
}
