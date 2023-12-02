package ru.astrainteractive.astrashop.gui.quicksell.presentation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astrashop.api.ShopApi
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.api.model.SpigotShopItemStack
import ru.astrainteractive.astrashop.core.PluginTranslation
import ru.astrainteractive.astrashop.domain.interactor.SellInteractor

class QuickSellController(
    private val translation: PluginTranslation,
    private val dataSource: ShopApi,
    private val sellInteractor: SellInteractor,
    translationContext: BukkitTranslationContext
) : AsyncComponent(), BukkitTranslationContext by translationContext {

    fun onItemClicked(e: InventoryClickEvent) {
        val itemStack = e.currentItem ?: return
        val player = e.whoClicked as Player
        componentScope.launch(Dispatchers.IO) {
            val item = dataSource.fetchShopList().map {
                it.items.values.firstOrNull {
                    isSimilar(it, itemStack)
                } to it
            }.firstOrNull { it.first != null } ?: run {
                e.whoClicked.sendMessage(translation.general.itemNotBuying)
                return@launch
            }
            val amount = if (e.isShiftClick) 64 else 1
            sellInteractor(SellInteractor.Param(amount, item.first!!, item.second, player))
        }
    }

    private fun isSimilar(shopItem: ShopConfig.ShopItem, itemStack: ItemStack): Boolean {
        return when (val shopItem = shopItem.shopItem) {
            is SpigotShopItemStack.ItemStackStack -> itemStack.isSimilar(shopItem.itemStack)
            is SpigotShopItemStack.Material -> itemStack.isSimilar(ItemStack(shopItem.material))
            else -> error("Not a spigot item")
        }
    }
}
