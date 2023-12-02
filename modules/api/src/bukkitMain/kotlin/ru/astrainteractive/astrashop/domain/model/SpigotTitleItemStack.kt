package ru.astrainteractive.astrashop.domain.model

import org.bukkit.Material

data class SpigotTitleItemStack(
    val material: Material,
    val customModelData: Int = 0,
    val name: String,
    val lore: List<String> = emptyList()
): TitleItemStack
