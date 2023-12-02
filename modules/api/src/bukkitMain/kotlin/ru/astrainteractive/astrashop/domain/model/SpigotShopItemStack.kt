package ru.astrainteractive.astrashop.domain.model

sealed interface SpigotShopItemStack : ShopItemStack {
    data class ItemStackStack(
        val itemStack: org.bukkit.inventory.ItemStack,
    ) : SpigotShopItemStack

    data class Material(
        val material: org.bukkit.Material
    ) : SpigotShopItemStack
}
