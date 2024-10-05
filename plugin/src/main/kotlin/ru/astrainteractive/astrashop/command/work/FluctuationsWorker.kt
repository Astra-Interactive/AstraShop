package ru.astrainteractive.astrashop.command.work

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.astrashop.api.ShopApi
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.core.Worker
import ru.astrainteractive.astrashop.domain.calculator.PriceCalculator
import kotlin.math.sign
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class FluctuationsWorker(
    private val shopApi: ShopApi,
    private val configName: String,
    private val options: ShopConfig.Options.Fluctuations,
    override val scope: CoroutineScope
) : Worker("FluctuationsWorker-$configName") {
    override val initialDelay: Duration = 0.milliseconds
    override val period: Duration = options.period

    override suspend fun doWork() {
        info { "#doWork Fluctuations happened in $configName" }
        val shop = shopApi.fetchShop(configName)
        shop.items
            .forEach { (_, shopItem) ->
                val median = PriceCalculator.fMedian(shopItem.price)
                val sign = shopItem.stock.minus(median).sign
                val change = sign.times(median).times(options.power).toInt()
                if (change == 0) return@forEach
                shopItem.stock = (shopItem.stock + change).coerceAtLeast(0)
            }
        shopApi.updateShop(shop)
    }
}
