package ru.astrainteractive.astrashop.domain.usecase

import ru.astrainteractive.astrashop.api.model.ShopConfig
import kotlin.math.sqrt

object PriceCalculator {

    private fun sch(x: Double): Double {
        return 1 / kotlin.math.cosh(x)
    }

    /**
     * Calculated a median - threshold for item dynamic price
     */
    private fun fMedian(x: Double): Double {
        return when {
            x <= 1 -> 50.0
            x <= 10 -> x * sqrt(x)
            x <= 100 -> (x / (1.0 + x)) * sqrt(x) + x / 2
            x <= 1000 -> (x / (1.0 + x)) * sqrt(x)
            else -> 2.0
        }
    }

    /**
     * Calculate price depending on it's
     * [stock] - amount in shop
     * [price] - static price value
     */
    private fun f(stock: Int, price: Double): Double {
        if (stock == -1) return price
        if (price <= 0) return 0.0
        if (stock == 0) return 0.0
        val median = fMedian(price)
        return sch(stock.toDouble() / median) * price
    }

    /**
     * Calculate sell price from shop
     *
     * aka player buy from shop for this price
     */
    fun calculateBuyPrice(item: ShopConfig.ShopItem, amount: Int): Double {
        if (!item.isForPurchase) return 0.0
        if (item.stock == 0 && !item.isPurchaseInfinite) return 0.0
        if (item.stock == -1) return item.price * amount
        val maxAmount = if (item.isPurchaseInfinite) amount else item.stock
        val coercedAmount = amount.coerceIn(0, maxAmount)
        return ((maxAmount - coercedAmount + 1)..maxAmount)
            .sumOf { stock -> f(stock, item.price * 1.6).coerceAtLeast(item.price) }
    }

    /**
     * Calculate sell price by player to shop
     *
     * aka player sell to shop for this price
     */
    fun calculateSellPrice(item: ShopConfig.ShopItem, amount: Int): Double {
        if (!item.isForSell) return 0.0
        if (item.stock == -1) return item.price * 0.7 * amount
        return ((item.stock + 1)..(item.stock + amount))
            .sumOf { stock -> f(stock, item.price * 0.7).coerceAtLeast(item.price * 0.05) }
    }
}
