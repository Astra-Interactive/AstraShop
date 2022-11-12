package ru.astrainteractive.astrashop.gui.buy

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.architecture.ViewModel
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.utils.economy.IEconomyProvider
import ru.astrainteractive.astrashop.domain.models.ShopConfig
import ru.astrainteractive.astrashop.domain.models.ShopItemStack
import ru.astrainteractive.astrashop.domain.models.ShopMaterial
import ru.astrainteractive.astrashop.modules.EconomyModule
import ru.astrainteractive.astrashop.utils.copy
import ru.astrainteractive.astrashop.utils.hasAtLeast
import ru.astrainteractive.astrashop.utils.toItemStack
import java.util.UUID


class BuyViewModel(val shopConfig: ShopConfig, val item: ShopConfig.ShopItem, val player: Player) : ViewModel() {
    private val economy by EconomyModule

    fun onBuyClicked(amount: Int) {
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
        if (notFittedItems.isEmpty()) return
        player.sendMessage("Некоторые предметы не вместились. Они лежат на полу")
        player.location.world.dropItemNaturally(player.location, itemStack.copy(notFittedItems.size))
    }

    fun onSellClicked(amount: Int) {
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


    }
}