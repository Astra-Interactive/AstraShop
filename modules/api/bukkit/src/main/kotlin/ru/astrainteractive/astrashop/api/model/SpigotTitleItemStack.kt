package ru.astrainteractive.astrashop.api.model

import org.bukkit.Material

sealed interface SpigotTitleItemStack : TitleItemStack {
    data class Default(
        val material: Material,
        val customModelData: Int = 0,
        val name: String,
        val lore: List<String> = emptyList()
    ) : SpigotTitleItemStack

    data class ItemsAdder(
        val namespaceId: String,
        val name: String,
        val lore: List<String> = emptyList()
    ) : SpigotTitleItemStack
}
