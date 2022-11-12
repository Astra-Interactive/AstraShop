package ru.astrainteractive.astrashop.gui.shops

import kotlinx.coroutines.runBlocking
import ru.astrainteractive.astralibs.architecture.ViewModel
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astrashop.modules.DataSourceModule

class ShopsViewModel : ViewModel() {
    private val dataSource by DataSourceModule
    val shops = runBlocking { dataSource.fetchShopList() }

}