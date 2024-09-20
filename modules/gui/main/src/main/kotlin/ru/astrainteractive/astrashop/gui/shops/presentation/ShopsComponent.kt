package ru.astrainteractive.astrashop.gui.shops.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import ru.astrainteractive.astrashop.api.model.ShopConfig

interface ShopsComponent : CoroutineScope {
    val model: StateFlow<Model>

    fun loadShops()

    sealed interface Model {
        val maxPages: Int

        data object Loading : Model {
            override val maxPages: Int = 0
        }

        class Loaded(val shops: List<ShopConfig>) : Model {
            override val maxPages: Int = shops.maxOf { it.options.page }
        }
    }
}
