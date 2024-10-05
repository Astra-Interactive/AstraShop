package ru.astrainteractive.astrashop.gui.buy.presentation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.async.CoroutineFeature
import ru.astrainteractive.astrashop.api.ShopApi
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.core.di.factory.CurrencyEconomyProviderFactory
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
    private val currencyEconomyProviderFactory: CurrencyEconomyProviderFactory,
    private val sellInteractor: SellInteractor,
    private val buyInteractor: BuyInteractor
) : CoroutineFeature by CoroutineFeature.Default(Dispatchers.IO), BuyComponent {

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
            val economy = when (val currencyId = item.buyCurrencyId) {
                null -> currencyEconomyProviderFactory.findDefault()
                else -> currencyEconomyProviderFactory.findByCurrencyId(currencyId)
            }
            if (economy == null) {
                error { "#invoke could not find currency with id ${item.buyCurrencyId} (or default currency)" }
            }
            model.value = Model.Loaded(
                item = item,
                shopConfig = shopConfig.options,
                instance = shopConfig,
                playerBalance = economy.getBalance(playerUUID)?.toInt() ?: 0
            )
        }
    }

    override fun onBuyClicked(amount: Int) {
        val state = model.value as? Model.Loaded ?: return
        launch(Dispatchers.IO) {
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
        launch(Dispatchers.IO) {
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
        launch(Dispatchers.IO) { loadItems() }
    }
}
