package ru.astrainteractive.astrashop.api.model

import ru.astrainteractive.astralibs.string.StringDesc
import kotlin.time.Duration

data class ShopConfig(
    val configName: String,
    val options: Options,
    val items: HashMap<String, ShopItem>
) {
    data class Options(
        val page: Int,
        val index: Int,
        val title: StringDesc.Raw,
        val titleItem: TitleItemStack,
        val stabilizing: Stabilizing,
        val fluctuations: Fluctuations
    ) {
        data class Stabilizing(
            val enabled: Boolean,
            val period: Duration,
            val power: Float
        )

        /**
         * Fluctuate items randomly
         */
        data class Fluctuations(
            val enabled: Boolean,
            val period: Duration,
            val power: Float
        )
    }

    data class ShopItem(
        val itemIndex: Int,
        val sellCurrencyId: String? = null,
        val buyCurrencyId: String? = null,
        val isForSell: Boolean,
        val isPurchaseInfinite: Boolean,
        val isForPurchase: Boolean,
        var stock: Int,
        val price: Double,
        val shopItem: ShopItemStack
    )
}
