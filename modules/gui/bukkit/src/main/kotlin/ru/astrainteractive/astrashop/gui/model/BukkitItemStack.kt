package ru.astrainteractive.astrashop.gui.model

import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.domain.util.ItemStackExt.toItemStack

internal class BukkitItemStack(private val itemStack: ItemStack) : SharedItemStack {
    override fun isSimilar(shopItem: ShopConfig.ShopItem): Boolean {
        return itemStack.isSimilar(shopItem.toItemStack())
    }
}
