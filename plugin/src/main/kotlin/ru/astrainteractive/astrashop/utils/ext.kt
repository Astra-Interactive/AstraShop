package ru.astrainteractive.astrashop.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import ru.astrainteractive.astralibs.async.BukkitMain
import ru.astrainteractive.astralibs.menu.menu.Menu
import ru.astrainteractive.astralibs.utils.economy.EconomyProvider
import ru.astrainteractive.astrashop.domain.models.ShopConfig
import ru.astrainteractive.astrashop.domain.models.SpigotShopItem
import ru.astrainteractive.astrashop.domain.models.SpigotTitleItem
import ru.astrainteractive.astrashop.gui.shops.ShopsGUI
import java.util.*

suspend inline fun Menu.openOnMainThread() = withContext(Dispatchers.BukkitMain) {
    open()
}

fun ShopsGUI.inventoryIndex(i: Int) = i + maxItemsPerPage * page

fun EconomyProvider.hasAtLeast(amount: Number, uuid: UUID): Boolean {
    val balance = getBalance(uuid) ?: 0.0
    return balance > amount.toDouble()
}

fun ShopConfig.ShopItem<SpigotShopItem>.toItemStack(): ItemStack = when (val shopItem = this.shopItem) {
    is SpigotShopItem.ItemStack -> shopItem.itemStack
    is SpigotShopItem.Material -> ItemStack(shopItem.material)
}

fun ItemStack.copy(amount: Int = this.amount) = clone().apply {
    this.amount = amount
}

fun SpigotTitleItem.toItemStack(): ItemStack {
    return ItemStack(material).apply {
        editMeta {
            it.setDisplayName(name)
            it.setCustomModelData(customModelData)
        }
        lore = this@toItemStack.lore
    }
}


fun ItemStack.isSimple(): Boolean {
    val itemMeta = itemMeta ?: return true
    return !hasItemMeta() ||
            (!itemMeta.hasDisplayName()
                    && !itemMeta.hasEnchants()
                    && !itemMeta.hasAttributeModifiers()
                    && !itemMeta.hasCustomModelData()
                    && !itemMeta.hasLore())
}

fun ItemStack.asShopItem(index: Int): ShopConfig.ShopItem<SpigotShopItem> {
    val spigotShopItem = if (isSimple()) SpigotShopItem.Material(type) else SpigotShopItem.ItemStack(this)
    return ShopConfig.ShopItem(
        itemIndex = index,
        shopItem = spigotShopItem,
        median = 0.0,
        stock = 0,
        buyPrice = 0.0,
        sellPrice = 0.0,
        priceMax = 0.0,
        priceMin = 0.0,
    )

}