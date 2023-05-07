package ru.astrainteractive.astrashop.domain.models

sealed interface SpigotShopItem {
    data class ItemStack(
        val itemStack: org.bukkit.inventory.ItemStack,
    ) : SpigotShopItem

    data class Material(
        val material: org.bukkit.Material
    ) : SpigotShopItem
}
