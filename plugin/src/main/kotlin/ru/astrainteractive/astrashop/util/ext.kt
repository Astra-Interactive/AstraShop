@file:Suppress("Filename")

package ru.astrainteractive.astrashop.util

import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.api.model.SpigotShopItemStack
import ru.astrainteractive.astrashop.api.model.SpigotTitleItemStack
import ru.astrainteractive.astrashop.api.model.TitleItemStack
import ru.astrainteractive.astrashop.gui.shops.ui.ShopsGUI
import java.util.UUID

fun ShopsGUI.inventoryIndex(i: Int) = i + maxItemsPerPage * page

fun EconomyProvider.hasAtLeast(amount: Number, uuid: UUID): Boolean {
    val balance = getBalance(uuid) ?: 0.0
    return balance > amount.toDouble()
}

fun ShopConfig.ShopItem.toItemStack(): ItemStack = when (val shopItem = this.shopItem) {
    is SpigotShopItemStack.ItemStackStack -> shopItem.itemStack
    is SpigotShopItemStack.Material -> ItemStack(shopItem.material)
    else -> error("Not a spigot item")
}

fun ItemStack.copy(amount: Int = this.amount) = clone().apply {
    this.amount = amount
}

fun TitleItemStack.toItemStack(): ItemStack {
    this as SpigotTitleItemStack
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
        (
            !itemMeta.hasDisplayName() &&
                !itemMeta.hasEnchants() &&
                !itemMeta.hasAttributeModifiers() &&
                !itemMeta.hasCustomModelData() &&
                !itemMeta.hasLore()
            )
}

fun ItemStack.asShopItem(index: Int): ShopConfig.ShopItem {
    val spigotShopItemStack = if (isSimple()) {
        SpigotShopItemStack.Material(type)
    } else {
        SpigotShopItemStack.ItemStackStack(
            this
        )
    }
    return ShopConfig.ShopItem(
        itemIndex = index,
        shopItem = spigotShopItemStack,
        median = 0.0,
        stock = 0,
        buyPrice = 0.0,
        sellPrice = 0.0,
        priceMax = 0.0,
        priceMin = 0.0,
    )
}
