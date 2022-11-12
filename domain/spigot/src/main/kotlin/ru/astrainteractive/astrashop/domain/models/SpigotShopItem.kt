package ru.astrainteractive.astrashop.domain.models

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

data class ShopItemStack(
    override val itemIndex: Int,
    override val median: Double,
    override var stock: Int,
    override val price: Double,
    override val priceMax: Double,
    override val priceMin: Double,
    val itemStack: ItemStack,
) : ShopConfig.ShopItem

data class ShopMaterial(
    override val itemIndex: Int,
    override val median: Double,
    override var stock: Int,
    override val price: Double,
    override val priceMax: Double,
    override val priceMin: Double,
    val material: Material
) : ShopConfig.ShopItem

data class SpigotTitleItem(
    val material: Material,
    val customModelData: Int,
    val name: String,
    val lore: List<String>
) : ShopConfig.TitleItem