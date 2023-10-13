package ru.astrainteractive.astrashop.gui.shops

import kotlinx.coroutines.flow.StateFlow
import ru.astrainteractive.astrashop.State
import ru.astrainteractive.astrashop.api.util.SpigotShopConfigAlias

interface ShopsComponent {
    val model: StateFlow<Model>

    sealed interface Model : State {
        val maxItemsAmount: Int

        data object Loading : Model {
            override val maxItemsAmount: Int = 0
        }

        class Loaded(val shops: List<SpigotShopConfigAlias>) : Model {
            override val maxItemsAmount: Int = shops.size
        }
    }
}
