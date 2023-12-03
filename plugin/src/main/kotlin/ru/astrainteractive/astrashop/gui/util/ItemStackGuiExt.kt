@file:Suppress("Filename")

package ru.astrainteractive.astrashop.gui.util

import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.api.model.SpigotShopItemStack
import ru.astrainteractive.astrashop.gui.shops.ui.ShopsGUI

object ItemStackGuiExt {
    fun ShopsGUI.inventoryIndex(i: Int) = i + maxItemsPerPage * page

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
}
