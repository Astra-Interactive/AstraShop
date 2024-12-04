package ru.astrainteractive.astrashop.domain.util

import dev.lone.itemsadder.api.CustomStack
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.api.model.SpigotShopItemStack
import ru.astrainteractive.astrashop.api.model.SpigotTitleItemStack
import ru.astrainteractive.astrashop.api.model.TitleItemStack

object ItemStackExt {

    fun ShopConfig.ShopItem.toItemStack(): ItemStack = when (val shopItem = this.shopItem) {
        is SpigotShopItemStack.ItemStackStack -> shopItem.itemStack
        is SpigotShopItemStack.Material -> ItemStack(shopItem.material)
        is SpigotShopItemStack.ItemsAdder -> {
            CustomStack.getInstance(shopItem.namespaceId)
                ?.itemStack
                ?.let { itemStack ->
                    // TODO temporal fix for items adder alpha
                    val clonedItemStack = ItemStack(itemStack.type, itemStack.amount)
                    clonedItemStack.itemMeta = itemStack.itemMeta.clone()
                    clonedItemStack
                }
                ?: error("Item ${shopItem.namespaceId} not found in itemsAdder registry")
        }

        else -> error("Not a spigot item")
    }

    fun ItemStack.copy(amount: Int = this.amount) = clone().apply {
        this.amount = amount
    }

    fun TitleItemStack.toItemStack(): ItemStack {
        return when (this) {
            is SpigotTitleItemStack.Default -> ItemStack(material).apply {
                editMeta {
                    it.setDisplayName(name)
                    it.setCustomModelData(customModelData)
                }
                lore = this@toItemStack.lore
            }

            is SpigotTitleItemStack.ItemsAdder -> {
                CustomStack.getInstance(namespaceId)
                    ?.itemStack
                    ?.let { itemStack ->
                        // TODO temporal fix for items adder alpha
                        val clonedItemStack = ItemStack(itemStack.type, itemStack.amount)
                        clonedItemStack.itemMeta = itemStack.itemMeta.clone()
                        clonedItemStack
                    }
                    ?.apply {
                        editMeta {
                            it.setDisplayName(name)
                        }
                        lore = this@toItemStack.lore
                    }
                    ?: error("Item $namespaceId not found in itemsAdder registry")
            }

            else -> error("${this::class} is not ${SpigotTitleItemStack::class}")
        }
    }
}
