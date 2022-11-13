package ru.astrainteractive.astrashop.utils

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import ru.astrainteractive.astralibs.menu.IPlayerHolder
import ru.astrainteractive.astralibs.utils.economy.IEconomyProvider
import ru.astrainteractive.astrashop.domain.models.ShopConfig
import ru.astrainteractive.astrashop.domain.models.ShopItemStack
import ru.astrainteractive.astrashop.domain.models.ShopMaterial
import ru.astrainteractive.astrashop.domain.models.SpigotTitleItem
import ru.astrainteractive.astrashop.gui.shops.ShopsGUI
import java.util.*

fun ShopsGUI.inventoryIndex(i: Int) = i + maxItemsPerPage * page

fun IEconomyProvider.hasAtLeast(amount: Number, uuid: UUID): Boolean {
    val balance = getBalance(uuid)?.toDouble() ?: 0.0
    return balance > amount.toDouble()
}

fun ShopConfig.ShopItem.toItemStack(): ItemStack = when (this) {
    is ShopItemStack -> itemStack
    is ShopMaterial -> ItemStack(material)
    else -> throw Exception("Not spigot item")
}

fun ItemStack.copy(amount: Int = this.amount) = clone().apply {
    this.amount = amount
}

fun ItemStack.withMeta(block: ItemMeta.() -> Unit): ItemStack = this.apply {
    editMeta(block)
}

fun ShopConfig.TitleItem.toItemStack(): ItemStack {
    val titleItem = this as SpigotTitleItem
    return ItemStack(titleItem.material).withMeta {
        setDisplayName(titleItem.name)
        setCustomModelData(titleItem.customModelData)
        this.lore = titleItem.lore
    }
}


fun ItemStack.isSimple(): Boolean {
    return !hasItemMeta() ||
            (!itemMeta.hasDisplayName()
                    && !itemMeta.hasEnchants()
                    && !itemMeta.hasAttributeModifiers()
                    && !itemMeta.hasCustomModelData()
                    && !itemMeta.hasLore())
}

fun ItemStack.asShopItem(index:Int): ShopConfig.ShopItem {
    return if (isSimple()) ShopMaterial(
        itemIndex = index,
        material = type
    ) else ShopItemStack(itemIndex = index, itemStack = this)

}