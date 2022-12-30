package ru.astrainteractive.astrashop.domain.models

import kotlin.math.roundToInt

data class ShopConfig(
    val configName: String,
    val options: Options,
    val items: HashMap<String, ShopItem>
) {
    data class Options(
        val lore: List<String>,
        val permission: String,
        val workHours: String,
        val title: String,
        val titleItem: TitleItem
    )

    interface TitleItem

    interface ShopItem {
        val itemIndex: Int
        val median: Double
        var stock: Int
        var buyPrice: Double
        var sellPrice: Double
        val priceMax: Double
        val priceMin: Double

        fun f(price: Double, amount: Int): Double {
            if (stock == -1) return price

            val expectedStock = stock - amount
            if (price <= 0) return 0.0
            if (expectedStock <= 0) return price
            return (price) / expectedStock
        }

        fun Number.round() = (this.toDouble() * 100.0).roundToInt() / 100.0
        fun calculateBuyPrice(amount: Int): Double = (amount downTo 1).sumOf { f(buyPrice, amount).coerceAtLeast(sellPrice) }.round()
        fun calculateSellPrice(amount: Int): Double = ((amount downTo 1).sumOf { f(sellPrice, amount).coerceAtMost(priceMin) } ).round()
    }
}