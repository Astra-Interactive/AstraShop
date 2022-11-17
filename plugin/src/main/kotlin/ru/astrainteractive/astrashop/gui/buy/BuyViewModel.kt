package ru.astrainteractive.astrashop.gui.buy

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.architecture.ViewModel
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.utils.economy.VaultEconomyProvider
import ru.astrainteractive.astrashop.asState
import ru.astrainteractive.astrashop.domain.interactors.BuyInteractor
import ru.astrainteractive.astrashop.domain.interactors.SellInteractor
import ru.astrainteractive.astrashop.domain.models.ShopItemStack
import ru.astrainteractive.astrashop.domain.models.ShopMaterial
import ru.astrainteractive.astrashop.domain.usecases.BuyUseCase
import ru.astrainteractive.astrashop.domain.usecases.ChangeStockAmountUseCase
import ru.astrainteractive.astrashop.domain.usecases.SellUseCase
import ru.astrainteractive.astrashop.modules.BuyInteractorModule
import ru.astrainteractive.astrashop.modules.DataSourceModule
import ru.astrainteractive.astrashop.modules.EconomyModule
import ru.astrainteractive.astrashop.modules.SellInteractorModule
import ru.astrainteractive.astrashop.utils.copy
import ru.astrainteractive.astrashop.utils.hasAtLeast
import ru.astrainteractive.astrashop.utils.toItemStack


class BuyViewModel(
    private val configName: String,
    private val itemIndex: Int,
    private val player: Player
) : ViewModel() {
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
        viewModelScope.launch(Dispatchers.IO) {
            buyInteractor.invoke(
                BuyInteractor.Param(
                    amount, state.item, state.instance, player
                )
            )
        }
    }

    fun onSellClicked(amount: Int) {
        val state = state.value.asState<BuyState.Loaded>() ?: return
        viewModelScope.launch(Dispatchers.IO) {
            sellInteractor.invoke(
                SellInteractor.Param(
                    amount, state.item, state.instance, player
                )
            )
            loadItems()
        }
    }


    init {
        viewModelScope.launch(Dispatchers.IO) { loadItems() }
    }
}