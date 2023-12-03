package ru.astrainteractive.astrashop.gui.util

import org.bukkit.Material
import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.menu.menu.Menu
import ru.astrainteractive.astralibs.menu.menu.PaginatedMenu
import ru.astrainteractive.astralibs.menu.menu.setDisplayName
import ru.astrainteractive.astralibs.menu.menu.setIndex
import ru.astrainteractive.astralibs.menu.menu.setMaterial
import ru.astrainteractive.astralibs.menu.menu.setOnClickListener
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astrashop.core.PluginTranslation

class Buttons(
    private val translation: PluginTranslation,
    private val translationContext: BukkitTranslationContext,
    private val menu: Menu
) : BukkitTranslationContext by translationContext {

    @Suppress("FunctionNaming")
    fun backButton(
        onClick: Click,
    ) = InventorySlot.Builder()
        .setIndex(49)
        .setMaterial(Material.PAPER)
        .setDisplayName(translation.buttons.buttonBack.toComponent())
        .setOnClickListener(onClick)
        .build()

    val nextButton: InventorySlot
        get() = InventorySlot.Builder()
            .setIndex(53)
            .setMaterial(Material.PAPER)
            .setDisplayName(translation.menu.menuNextPage.toComponent())
            .setOnClickListener { (menu as PaginatedMenu).showNextPage() }
            .build()

    val prevButton: InventorySlot
        get() = InventorySlot.Builder()
            .setIndex(45)
            .setMaterial(Material.PAPER)
            .setDisplayName(translation.menu.menuPrevPage.toComponent())
            .setOnClickListener { (menu as PaginatedMenu).showPrevPage() }
            .build()
}
