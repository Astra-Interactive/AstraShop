package ru.astrainteractive.astrashop.gui.buy

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astrashop.asState
import ru.astrainteractive.astrashop.di.RootModule
import ru.astrainteractive.astrashop.di.impl.RootModuleImpl
import ru.astrainteractive.astrashop.domain.interactor.BuyInteractor
import ru.astrainteractive.astrashop.domain.interactor.SellInteractor
import ru.astrainteractive.klibs.kdi.getValue

class BuyViewModel(
    private val configName: String,
    private val itemIndex: Int,
    private val player: Player,
) : AsyncComponent() {

    private val rootModule: RootModule = RootModuleImpl
    private val economy by rootModule.economyProvider
    private val dataSource by rootModule.spigotShopApi
    private val buyInteractor = rootModule.domainModule.buyInteractor.create()
    private val sellInteractor = rootModule.domainModule.sellInteractor.create()

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
                    amount,
                    state.item,
                    state.instance,
                    player
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
                    amount,
                    state.item,
                    state.instance,
                    player
                )
            )
            loadItems()
        }
    }

    init {
        componentScope.launch(Dispatchers.IO) { loadItems() }
    }
}
