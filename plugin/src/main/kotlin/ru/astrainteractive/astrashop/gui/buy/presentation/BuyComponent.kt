package ru.astrainteractive.astrashop.gui.buy.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import ru.astrainteractive.astrashop.api.model.ShopConfig

interface BuyComponent : CoroutineScope {
    val model: StateFlow<Model>

    fun onBuyClicked(amount: Int)

    fun onSellClicked(amount: Int)

    sealed interface Model {
        data object Loading : Model

        data object Error : Model

        data class Loaded(
            val item: ShopConfig.ShopItem,
            val shopConfig: ShopConfig.Options,
            val instance: ShopConfig,
            val playerBalance: Int
        ) : Model
    }
}
