package ru.astrainteractive.astrashop.domain.usecase

import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.api.model.ShopItemStack
import kotlin.test.Test
import kotlin.test.assertTrue

class PriceCalculatorTest {

    private fun <T : Number> assertLessOrEqual(expected: T, actual: T) {
        assertTrue("Expected $expected <= $actual") { expected.toDouble() <= actual.toDouble() }
    }

    private fun <T : Number> assertGreater(expected: T, actual: T) {
        assertTrue("Expected $expected >= $actual") { expected.toDouble() >= actual.toDouble() }
    }

    @Test
    fun `buy batch @ sell by one @ same price`() {
        val shopItem = ShopConfig.ShopItem(
            itemIndex = 0,
            isForPurchase = true,
            isForSell = true,
            stock = 4,
            price = 100.0,
            shopItem = ShopItemStack.Stub,
            isPurchaseInfinite = false
        )
        val buyPrice = PriceCalculator.calculateBuyPrice(shopItem, 4)
        shopItem.stock = 0
        val sellPrice = intArrayOf(1, 1, 1, 1).sumOf { amount ->
            val res = PriceCalculator.calculateSellPrice(shopItem, amount)
            shopItem.stock += 1
            res
        }
        assertGreater(buyPrice, sellPrice)
    }

    @Test
    fun `buy batch @ sell batch @ same price`() {
        val shopItem = ShopConfig.ShopItem(
            itemIndex = 0,
            isForPurchase = true,
            isForSell = true,
            stock = 4,
            price = 100.0,
            shopItem = ShopItemStack.Stub,
            isPurchaseInfinite = false
        )
        val buyPrice = PriceCalculator.calculateBuyPrice(shopItem, 4)
        shopItem.stock = 0
        val sellPrice = PriceCalculator.calculateSellPrice(shopItem, 4)
        assertGreater(buyPrice, sellPrice)
    }

    @Test
    fun `buy batch when not enough @ sell batch @ same price`() {
        val shopItem = ShopConfig.ShopItem(
            itemIndex = 0,
            isForPurchase = true,
            isForSell = true,
            stock = 2,
            price = 100.0,
            shopItem = ShopItemStack.Stub,
            isPurchaseInfinite = false
        )
        val buyPrice = PriceCalculator.calculateBuyPrice(shopItem, 3)
        shopItem.stock = 0
        val sellPrice = PriceCalculator.calculateSellPrice(shopItem, 2)
        assertGreater(buyPrice, sellPrice)
    }

    @Test
    fun `buy batch when not enough @ sell by one @ same price`() {
        val shopItem = ShopConfig.ShopItem(
            itemIndex = 0,
            isForPurchase = true,
            isForSell = true,
            stock = 2,
            price = 100.0,
            shopItem = ShopItemStack.Stub,
            isPurchaseInfinite = false
        )
        val buyPrice = PriceCalculator.calculateBuyPrice(shopItem, 3)
        shopItem.stock = 0
        val sellPrice = intArrayOf(1, 1).sumOf { amount ->
            val res = PriceCalculator.calculateSellPrice(shopItem, amount)
            shopItem.stock += 1
            res
        }
        assertGreater(buyPrice, sellPrice)
    }

    @Test
    fun `buy batch when more than enough @ sell batch @ same price`() {
        val shopItem = ShopConfig.ShopItem(
            itemIndex = 0,
            isForPurchase = true,
            isForSell = true,
            stock = 10,
            price = 100.0,
            shopItem = ShopItemStack.Stub,
            isPurchaseInfinite = false
        )
        val buyPrice = PriceCalculator.calculateBuyPrice(shopItem, 3)
        shopItem.stock = 7
        val sellPrice = PriceCalculator.calculateSellPrice(shopItem, 3)
        assertGreater(buyPrice, sellPrice)
    }

    @Test
    fun `buy batch when more than enough @ sell by one @ same price`() {
        val shopItem = ShopConfig.ShopItem(
            itemIndex = 0,
            isForPurchase = true,
            isForSell = true,
            stock = 10,
            price = 100.0,
            shopItem = ShopItemStack.Stub,
            isPurchaseInfinite = false
        )
        val buyPrice = PriceCalculator.calculateBuyPrice(shopItem, 3)
        shopItem.stock = 7
        val sellPrice = intArrayOf(1, 1, 1).sumOf { amount ->
            val res = PriceCalculator.calculateSellPrice(shopItem, amount)
            shopItem.stock += 1
            res
        }
        assertGreater(buyPrice, sellPrice)
    }

    @Test
    fun `assert price lower`() {
        val shopItem = ShopConfig.ShopItem(
            itemIndex = 0,
            isForPurchase = true,
            isForSell = true,
            stock = 10,
            price = 100.0,
            shopItem = ShopItemStack.Stub,
            isPurchaseInfinite = false
        )
        (shopItem.stock downTo 2).forEach { _ ->
            val oldPrice = PriceCalculator.calculateBuyPrice(shopItem, 1)
            shopItem.stock -= 1
            val newPrice = PriceCalculator.calculateBuyPrice(shopItem, 1)
            assertLessOrEqual(oldPrice, newPrice)
        }
    }

    @Test
    fun `test stack costs more than one`() {
        listOf(-1, 1, 2).map { i ->
            val shopItem = ShopConfig.ShopItem(
                itemIndex = 0,
                isForPurchase = true,
                isForSell = true,
                stock = i,
                price = 1.04,
                shopItem = ShopItemStack.Stub,
                isPurchaseInfinite = true
            )
            val stackPrice = PriceCalculator.calculateBuyPrice(shopItem, 64)
            val onePrice = PriceCalculator.calculateBuyPrice(shopItem, 1)
            assertGreater(stackPrice, onePrice)
        }
    }

    @Test
    fun `test stack of stock 0 costs more than stack sell`() {
        val shopItem = ShopConfig.ShopItem(
            itemIndex = 0,
            isForPurchase = true,
            isForSell = true,
            stock = 0,
            price = 1.04,
            shopItem = ShopItemStack.Stub,
            isPurchaseInfinite = true
        )
        val stackBuyPrice = PriceCalculator.calculateBuyPrice(shopItem, 64)
        val oneBuyPrice = PriceCalculator.calculateBuyPrice(shopItem, 1)
        assertGreater(stackBuyPrice, oneBuyPrice)
        val stackSellPrice = PriceCalculator.calculateSellPrice(shopItem, 64)
        val oneSellPrice = PriceCalculator.calculateSellPrice(shopItem, 1)
        assertGreater(stackBuyPrice, stackSellPrice)
        assertGreater(oneBuyPrice, oneSellPrice)
    }
}
