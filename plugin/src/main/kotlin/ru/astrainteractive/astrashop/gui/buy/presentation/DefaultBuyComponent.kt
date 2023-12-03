package ru.astrainteractive.astrashop.gui.buy.presentation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astrashop.api.ShopApi
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.domain.interactor.BuyInteractor
import ru.astrainteractive.astrashop.domain.interactor.SellInteractor
import ru.astrainteractive.astrashop.gui.buy.presentation.BuyComponent.Model
import java.util.UUID

@Suppress("LongParameterList")
class DefaultBuyComponent(
    private val shopFileName: String,
    private val shopItem: ShopConfig.ShopItem,
    private val playerUUID: UUID,
    private val shopApi: ShopApi,
    private val economy: EconomyProvider,
    private val sellInteractor: SellInteractor,
    private val buyInteractor: BuyInteractor
) : AsyncComponent(), BuyComponent {

    override val model = MutableStateFlow<Model>(Model.Loading)

    private suspend fun loadItems() {
        model.value = Model.Loading
        val shopConfig = shopApi.fetchShop(shopFileName)

        val item = shopConfig.items.mapNotNull { (_, shopItem) ->
            if (shopItem.itemIndex == this.shopItem.itemIndex) {
                shopItem
            } else {
                null
            }
        }.firstOrNull()
        if (item == null) {
            model.value = Model.Error
        } else {
            model.value = Model.Loaded(
                item,
                shopConfig.options,
                shopConfig,
                economy.getBalance(playerUUID)?.toInt() ?: 0
            )
        }
    }

    override fun onBuyClicked(amount: Int) {
        val state = model.value as? Model.Loaded ?: return
        componentScope.launch(Dispatchers.IO) {
            buyInteractor.invoke(
                BuyInteractor.Param(
                    amount,
                    state.item,
                    state.instance,
                    playerUUID
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
                    playerUUID
                )
            )
            loadItems()
        }
    }

    init {
        componentScope.launch(Dispatchers.IO) { loadItems() }
    }
}
