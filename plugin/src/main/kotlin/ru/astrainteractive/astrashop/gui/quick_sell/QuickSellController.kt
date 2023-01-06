package ru.astrainteractive.astrashop.gui.quick_sell

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astrashop.domain.interactors.SellInteractor
import ru.astrainteractive.astrashop.domain.models.ShopConfig
import ru.astrainteractive.astrashop.domain.models.ShopItemStack
import ru.astrainteractive.astrashop.domain.models.ShopMaterial
import ru.astrainteractive.astrashop.modules.*

class QuickSellController : AsyncComponent() {
    private val economy by EconomyModule
    private val dataSource by DataSourceModule
    private val buyInteractor by BuyInteractorModule
    private val sellInteractor by SellInteractorModule
    private val translation by TranslationModule
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

    fun isSimilar(shopItem: ShopConfig.ShopItem, itemStack: ItemStack) = when (shopItem) {
        is ShopMaterial -> itemStack.isSimilar(ItemStack(shopItem.material))

        is ShopItemStack -> itemStack.isSimilar(shopItem.itemStack)

        else -> throw NotImplementedError()
    }
}