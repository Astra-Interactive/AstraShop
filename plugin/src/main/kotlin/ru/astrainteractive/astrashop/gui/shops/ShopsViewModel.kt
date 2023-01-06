package ru.astrainteractive.astrashop.gui.shops

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astrashop.asState
import ru.astrainteractive.astrashop.domain.models.ShopConfig
import ru.astrainteractive.astrashop.modules.DataSourceModule


class ShopsViewModel : AsyncComponent() {
    private val dataSource by DataSourceModule
    val state = MutableStateFlow<ShopsState>(ShopsState.Loading)
    val maxItemsAmount:Int
        get() = state.value.asState<ShopsState.Loaded>()?.shops?.size?:0
    init {
        componentScope.launch(Dispatchers.IO) {
            val list = dataSource.fetchShopList() ?: emptyList()
            state.value = ShopsState.Loaded(list)
        }
    }

}