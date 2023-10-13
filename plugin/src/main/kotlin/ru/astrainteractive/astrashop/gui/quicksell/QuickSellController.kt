package ru.astrainteractive.astrashop.gui.quicksell

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astrashop.api.interactors.SellInteractor
import ru.astrainteractive.astrashop.api.model.SpigotShopItem
import ru.astrainteractive.astrashop.api.util.SpigotShopItemAlias
import ru.astrainteractive.astrashop.di.impl.InteractorsFactoryModuleImpl
import ru.astrainteractive.astrashop.di.impl.RootModuleImpl
import ru.astrainteractive.klibs.kdi.getValue

class QuickSellController : AsyncComponent() {
    private val translation by RootModuleImpl.translation
    private val dataSource by RootModuleImpl.spigotShopApi
    private val sellInteractor = InteractorsFactoryModuleImpl.sellInteractor.create()

    fun onItemClicked(e: InventoryClickEvent) {
        val itemStack = e.currentItem ?: return
        val player = e.whoClicked as Player
        componentScope.launch(Dispatchers.IO) {
            val item = dataSource.fetchShopList().map {
                it.items.values.firstOrNull {
                    isSimilar(it, itemStack)
                } to it
            }.firstOrNull { it.first != null } ?: run {
                e.whoClicked.sendMessage(translation.itemNotBuying)
                return@launch
            }
            val amount = if (e.isShiftClick) 64 else 1
            sellInteractor(SellInteractor.Param(amount, item.first!!, item.second, player))
        }
    }

    fun isSimilar(shopItem: SpigotShopItemAlias, itemStack: ItemStack) = when (val shopItem = shopItem.shopItem) {
        is SpigotShopItem.ItemStack -> itemStack.isSimilar(shopItem.itemStack)
        is SpigotShopItem.Material -> itemStack.isSimilar(ItemStack(shopItem.material))
    }
}
