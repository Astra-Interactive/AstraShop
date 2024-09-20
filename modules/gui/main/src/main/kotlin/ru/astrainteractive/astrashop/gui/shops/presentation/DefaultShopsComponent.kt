package ru.astrainteractive.astrashop.gui.shops.presentation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astrashop.api.ShopApi
import ru.astrainteractive.astrashop.gui.shops.presentation.ShopsComponent.Model

internal class DefaultShopsComponent(
    private val api: ShopApi
) : AsyncComponent(), ShopsComponent {

    override val model = MutableStateFlow<Model>(Model.Loading)

    override fun loadShops() {
        componentScope.launch(Dispatchers.IO) {
            val list = api.fetchShopList()
            model.value = Model.Loaded(list)
        }
    }
}
