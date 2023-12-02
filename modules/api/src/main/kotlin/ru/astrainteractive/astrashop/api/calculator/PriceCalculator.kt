package ru.astrainteractive.astrashop.api.calculator

import ru.astrainteractive.astrashop.api.model.ShopConfig
import kotlin.math.roundToInt

object PriceCalculator {

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
}
