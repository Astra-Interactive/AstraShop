package ru.astrainteractive.astrashop.gui.shop.presentation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.async.CoroutineFeature
import ru.astrainteractive.astrashop.api.ShopApi
import ru.astrainteractive.astrashop.gui.shop.presentation.ShopComponent.Model

internal class DefaultShopComponent(
    private val shopFileName: String,
    private val shopApi: ShopApi,
) : CoroutineFeature by CoroutineFeature.Default(Dispatchers.IO), ShopComponent {

    override val model = MutableStateFlow<Model>(Model.Loading)

    private fun load() = launch(Dispatchers.IO) {
        model.value = Model.Loading
        val config = shopApi.fetchShop(shopFileName)
        model.value = Model.List(config)
    }

    init {
        load()
    }
}
