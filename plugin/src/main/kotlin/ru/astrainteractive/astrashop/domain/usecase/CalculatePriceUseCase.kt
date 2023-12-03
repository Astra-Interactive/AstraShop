package ru.astrainteractive.astrashop.domain.usecase

import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.domain.usecase.CalculatePriceUseCase.Input
import ru.astrainteractive.astrashop.domain.usecase.CalculatePriceUseCase.Output
import ru.astrainteractive.klibs.mikro.core.domain.UseCase
import kotlin.math.roundToInt

class CalculatePriceUseCase : UseCase.Blocking<Input, Output> {
    enum class Type {
        Buy, Sell
    }

    class Output(val price: Double)

    class Input(
        val item: ShopConfig.ShopItem,
        val amount: Int,
        val type: Type
    )

    private fun f(item: ShopConfig.ShopItem, price: Double, amount: Int): Double {
        if (item.stock == -1) return price

        val expectedStock = item.stock - amount
        if (price <= 0) return 0.0
        if (expectedStock <= 0) return price
        return (price) / expectedStock
    }

    private fun Number.round() = (this.toDouble() * 100.0).roundToInt() / 100.0

    fun calculateBuyPrice(item: ShopConfig.ShopItem, amount: Int): Double {
        return (amount downTo 1).sumOf { f(item, item.buyPrice, amount).coerceAtLeast(item.sellPrice) }.round()
    }

    fun calculateSellPrice(item: ShopConfig.ShopItem, amount: Int): Double {
        return ((amount downTo 1).sumOf { f(item, item.sellPrice, amount).coerceAtMost(item.priceMin) }).round()
    }

    override fun invoke(input: Input): Output {
        return when (input.type) {
            Type.Buy -> calculateBuyPrice(
                item = input.item,
                amount = input.amount
            )

            Type.Sell -> calculateSellPrice(
                item = input.item,
                amount = input.amount
            )
        }.let(::Output)
    }
}
