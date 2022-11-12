package ru.astrainteractive.astrashop.gui.buy

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.architecture.ViewModel
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astrashop.domain.models.ShopConfig
import ru.astrainteractive.astrashop.domain.models.ShopItemStack
import ru.astrainteractive.astrashop.domain.models.ShopMaterial
import ru.astrainteractive.astrashop.modules.DataSourceModule
import ru.astrainteractive.astrashop.modules.EconomyModule
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
    val state = MutableStateFlow<BuyState>(BuyState.Loading)
    private fun loadItems() = viewModelScope.launch(Dispatchers.IO) {
        val instance = dataSource.fetchShop(configName)
        val item = instance.items[itemIndex.toString()]!!
        state.value = BuyState.Loading
        state.value = BuyState.Loaded(
            item,
            instance.options,
            instance
        )
    }


    fun onBuyClicked(amount: Int) {
        val state = state.value as? BuyState.Loaded ?: return
        val item = state.item
        if (item.stock<=1){
            player.sendMessage("В магазине недостаточно предметов")
            return
        }
        if (!economy.hasAtLeast(19, player.uniqueId)) {
            player.sendMessage("Недостаточно денег")
            return
        }
        if (!economy.takeMoney(player.uniqueId, item.price)) {
            player.sendMessage("Недостаточно денег")
            return
        }
        val itemStack = item.toItemStack().copy(amount)
        val notFittedItems = player.inventory.addItem(itemStack)
        changeStockAmount(-amount)
        if (notFittedItems.isEmpty()) return
        player.sendMessage("Некоторые предметы не вместились. Они лежат на полу")
        player.location.world.dropItemNaturally(player.location, itemStack.copy(notFittedItems.size))
    }

    fun onSellClicked(amount: Int) {
        val state = state.value as? BuyState.Loaded ?: return
        val item = state.item
        val itemStack = when (item) {
            is ShopItemStack -> {
                if (!player.inventory.contains(item.itemStack)) {
                    player.sendMessage("У вас нет такого предмета")
                    return
                }
                item.itemStack
            }

            is ShopMaterial -> {
                if (!player.inventory.contains(item.material)) {
                    player.sendMessage("У вас нет такого предмета")
                    return
                }
                ItemStack(item.material)
            }

            else -> throw Exception("Not spigot item")
        }
        for (i in 0 until amount) {
            player.inventory.removeItem(itemStack)
        }
        val money = amount * item.price
        economy.addMoney(player.uniqueId, money)
        player.sendMessage("Вы получили $money\$")

        changeStockAmount(+amount)
    }

    private fun changeStockAmount(i: Int) = viewModelScope.launch(Dispatchers.IO) {
        val state = state.value as? BuyState.Loaded ?: return@launch
        val item = state.item

        val newAmount = (item.stock + i).coerceAtLeast(1)
        item.stock = newAmount
        dataSource.updateShop(state.instance)
        loadItems()
    }

    init {
        loadItems()
    }
}