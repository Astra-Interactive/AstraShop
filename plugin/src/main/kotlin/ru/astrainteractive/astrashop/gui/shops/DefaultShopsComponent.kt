package ru.astrainteractive.astrashop.gui.shops

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astrashop.di.impl.RootModuleImpl
import ru.astrainteractive.klibs.kdi.getValue

class DefaultShopsComponent : AsyncComponent(), ShopsComponent {
    private val dataSource by RootModuleImpl.spigotShopApi

    override val model = MutableStateFlow<ShopsComponent.Model>(ShopsComponent.Model.Loading)

    init {
        componentScope.launch(Dispatchers.IO) {
            val list = dataSource.fetchShopList() ?: emptyList()
            model.value = ShopsComponent.Model.Loaded(list)
        }
    }
}
