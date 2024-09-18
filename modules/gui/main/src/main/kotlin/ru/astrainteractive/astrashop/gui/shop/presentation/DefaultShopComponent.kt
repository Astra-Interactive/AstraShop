package ru.astrainteractive.astrashop.gui.shop.presentation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astrashop.api.ShopApi
import ru.astrainteractive.astrashop.gui.shop.presentation.ShopComponent.Model

internal class DefaultShopComponent(
    private val shopFileName: String,
    private val shopApi: ShopApi,
) : AsyncComponent(), ShopComponent {

    override val model = MutableStateFlow<Model>(Model.Loading)

    private fun load() = componentScope.launch(Dispatchers.IO) {
        model.value = Model.Loading
        val config = shopApi.fetchShop(shopFileName)
        model.value = Model.List(config)
    }

    init {
        load()
    }
}
