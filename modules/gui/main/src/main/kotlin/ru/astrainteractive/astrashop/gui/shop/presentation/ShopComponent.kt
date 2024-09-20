package ru.astrainteractive.astrashop.gui.shop.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import ru.astrainteractive.astrashop.api.model.ShopConfig

interface ShopComponent : CoroutineScope {
    val model: StateFlow<Model>

    sealed interface Model {
        val maxItemsAmount: Int

        data object Loading : Model {
            override val maxItemsAmount: Int = 0
        }

        data class List(val config: ShopConfig) : Model {
            val items: HashMap<String, ShopConfig.ShopItem> = config.items

            override val maxItemsAmount: Int = items.keys
                .mapNotNull(String::toIntOrNull)
                .maxOrNull() ?: 0
        }
    }
}
