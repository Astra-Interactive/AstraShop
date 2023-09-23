package ru.astrainteractive.astrashop.gui.shops

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astrashop.asState
import ru.astrainteractive.astrashop.di.impl.RootModuleImpl
import ru.astrainteractive.klibs.kdi.getValue

class ShopsViewModel : AsyncComponent() {
    private val dataSource by RootModuleImpl.spigotShopApi

    val state = MutableStateFlow<ShopsState>(ShopsState.Loading)
    val maxItemsAmount: Int
        get() = state.value.asState<ShopsState.Loaded>()?.shops?.size ?: 0
    init {
        componentScope.launch(Dispatchers.IO) {
            val list = dataSource.fetchShopList() ?: emptyList()
            state.value = ShopsState.Loaded(list)
        }
    }
}
