package ru.astrainteractive.astrashop.gui.buy

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astrashop.asState
import ru.astrainteractive.astrashop.domain.interactors.BuyInteractor
import ru.astrainteractive.astrashop.domain.interactors.SellInteractor
import ru.astrainteractive.astrashop.modules.BuyInteractorModule
import ru.astrainteractive.astrashop.modules.DataSourceModule
import ru.astrainteractive.astrashop.modules.EconomyModule
import ru.astrainteractive.astrashop.modules.SellInteractorModule


class BuyViewModel(
    private val configName: String,
    private val itemIndex: Int,
    private val player: Player
) : AsyncComponent() {
    private val economy by EconomyModule
    private val dataSource by DataSourceModule
    private val buyInteractor by BuyInteractorModule
    private val sellInteractor by SellInteractorModule
    val state = MutableStateFlow<BuyState>(BuyState.Loading)

    private suspend fun loadItems() {
        state.value = BuyState.Loading
        val instance = dataSource.fetchShop(configName)
        val item = instance.items[itemIndex.toString()]!!
        state.value = BuyState.Loaded(
            item,
            instance.options,
            instance,
            economy.getBalance(player.uniqueId)?.toInt() ?: 0
        )
    }


    fun onBuyClicked(amount: Int) {
        val state = state.value.asState<BuyState.Loaded>() ?: return
        componentScope.launch(Dispatchers.IO) {
            buyInteractor.invoke(
                BuyInteractor.Param(
                    amount, state.item, state.instance, player
                )
            )
            loadItems()
        }
    }

    fun onSellClicked(amount: Int) {
        val state = state.value.asState<BuyState.Loaded>() ?: return
        componentScope.launch(Dispatchers.IO) {
            sellInteractor.invoke(
                SellInteractor.Param(
                    amount, state.item, state.instance, player
                )
            )
            loadItems()
        }
    }


    init {
        componentScope.launch(Dispatchers.IO) { loadItems() }
    }
}