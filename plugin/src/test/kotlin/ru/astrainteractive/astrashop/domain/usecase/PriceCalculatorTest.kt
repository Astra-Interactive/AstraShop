package ru.astrainteractive.astrashop.domain.usecase

import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.api.model.ShopItemStack
import kotlin.test.Test
import kotlin.test.assertTrue

class PriceCalculatorTest {

    private fun <T : Number> assertLess(expected: T, actual: T) {
        assertTrue("Expected $expected < $actual") { expected.toDouble() < actual.toDouble() }
    }

    private fun <T : Number> assertGreater(expected: T, actual: T) {
        assertTrue("Expected $expected > $actual") { expected.toDouble() > actual.toDouble() }
    }

    @Test
    fun `buy batch @ sell by one @ same price`() {
        val shopItem = ShopConfig.ShopItem(
            itemIndex = 0,
            isForPurchase = true,
            isForSell = true,
            stock = 4,
            price = 100.0,
            shopItem = ShopItemStack.Stub
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
            shopItem = ShopItemStack.Stub
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
            shopItem = ShopItemStack.Stub
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
            shopItem = ShopItemStack.Stub
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
            shopItem = ShopItemStack.Stub
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
            shopItem = ShopItemStack.Stub
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
            shopItem = ShopItemStack.Stub
        )
        (shopItem.stock downTo 2).forEach { _ ->
            val oldPrice = PriceCalculator.calculateBuyPrice(shopItem, 1)
            shopItem.stock -= 1
            val newPrice = PriceCalculator.calculateBuyPrice(shopItem, 1)
            assertLess(oldPrice, newPrice)
        }
    }
}
