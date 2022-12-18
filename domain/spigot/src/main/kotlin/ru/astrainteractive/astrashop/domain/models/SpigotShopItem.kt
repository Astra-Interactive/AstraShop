package ru.astrainteractive.astrashop.domain.models

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

data class ShopItemStack(
    override val itemIndex: Int,
    override val median: Double = -1.0,
    override var stock: Int = -1,
    override var buyPrice: Double = -1.0,
    override var sellPrice: Double = -1.0,
    override val priceMax: Double = Double.MAX_VALUE,
    override val priceMin: Double = 0.0,
    val itemStack: ItemStack,
) : ShopConfig.ShopItem

data class ShopMaterial(
    override val itemIndex: Int,
    override val median: Double = -1.0,
    override var stock: Int = -1,
    override var buyPrice: Double = -1.0,
    override var sellPrice: Double = -1.0,
    override val priceMax: Double = Double.MAX_VALUE,
    override val priceMin: Double = 0.0,
    val material: Material
) : ShopConfig.ShopItem

data class SpigotTitleItem(
    val material: Material,
    val customModelData: Int = 0,
    val name: String,
    val lore: List<String> = emptyList()
) : ShopConfig.TitleItem