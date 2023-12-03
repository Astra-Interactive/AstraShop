package ru.astrainteractive.astrashop.domain.util

import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.api.model.SpigotShopItemStack
import ru.astrainteractive.astrashop.api.model.SpigotTitleItemStack
import ru.astrainteractive.astrashop.api.model.TitleItemStack

object ItemStackExt {

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
}
