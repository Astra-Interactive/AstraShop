package ru.astrainteractive.astrashop.command.work

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.astrashop.api.ShopApi
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.core.Worker
import ru.astrainteractive.astrashop.domain.calculator.PriceCalculator
import kotlin.math.abs
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class StabilizingWorker(
    private val shopApi: ShopApi,
    private val configName: String,
    private val options: ShopConfig.Options.Stabilizing,
    override val scope: CoroutineScope
) : Worker("StabilizingWorker-$configName") {
    override val initialDelay: Duration = 0.milliseconds
    override val period: Duration = options.period

    override suspend fun doWork() {
        info { "#doWork Stabilizing happened in $configName" }
        val shop = shopApi.fetchShop(configName)
        shop.items
            .forEach { (_, shopItem) ->
                val median = PriceCalculator.fMedian(shopItem.price).toInt()
                val diff = abs(shopItem.stock - median)
                if (diff == 0) return
                val sign = if (Random.nextBoolean()) -1 else 1
                val change = sign.times(diff)
                    .times(options.power)
                    .toInt()
                    .coerceAtLeast(1)
                shopItem.stock = (shopItem.stock + change).coerceAtLeast(0)
            }
        shopApi.updateShop(shop)
    }
}
