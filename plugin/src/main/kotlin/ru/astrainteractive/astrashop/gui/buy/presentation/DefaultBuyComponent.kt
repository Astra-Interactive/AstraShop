package ru.astrainteractive.astrashop.gui.buy.presentation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astrashop.api.ShopApi
import ru.astrainteractive.astrashop.domain.interactor.BuyInteractor
import ru.astrainteractive.astrashop.domain.interactor.SellInteractor
import ru.astrainteractive.astrashop.gui.buy.presentation.BuyComponent.Model

@Suppress("LongParameterList")
class DefaultBuyComponent(
    private val configName: String,
    private val itemIndex: Int,
    private val player: Player,
    private val dataSource: ShopApi,
    private val economy: EconomyProvider,
    private val sellInteractor: SellInteractor,
    private val buyInteractor: BuyInteractor
) : AsyncComponent(), BuyComponent {

    override val model = MutableStateFlow<Model>(Model.Loading)

    private suspend fun loadItems() {
        model.value = Model.Loading
        val instance = dataSource.fetchShop(configName)
        val item = instance.items[itemIndex.toString()]!!
        model.value = Model.Loaded(
            item,
            instance.options,
            instance,
            economy.getBalance(player.uniqueId)?.toInt() ?: 0
        )
    }

    override fun onBuyClicked(amount: Int) {
        val state = model.value as? Model.Loaded ?: return
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

    override fun onSellClicked(amount: Int) {
        val state = model.value as? Model.Loaded ?: return
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
