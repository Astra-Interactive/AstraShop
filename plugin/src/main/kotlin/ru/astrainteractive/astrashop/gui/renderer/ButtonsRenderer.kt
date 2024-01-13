package ru.astrainteractive.astrashop.gui.renderer

import org.bukkit.Material
import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.menu.menu.Menu
import ru.astrainteractive.astralibs.menu.menu.PaginatedMenu
import ru.astrainteractive.astralibs.menu.menu.setDisplayName
import ru.astrainteractive.astralibs.menu.menu.setIndex
import ru.astrainteractive.astralibs.menu.menu.setMaterial
import ru.astrainteractive.astralibs.menu.menu.setOnClickListener
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astrashop.core.PluginTranslation

class ButtonsRenderer(
    private val translation: PluginTranslation,
    private val menu: Menu,
    private val kyoriComponentSerializer: KyoriComponentSerializer
) : KyoriComponentSerializer by kyoriComponentSerializer {

    fun backButton(
        onClick: Click,
    ) = InventorySlot.Builder()
        .setIndex(49)
        .setMaterial(Material.PAPER)
        .setDisplayName(translation.buttons.buttonBack.let(::toComponent))
        .setOnClickListener(onClick)
        .build()

    val nextButton: InventorySlot
        get() = InventorySlot.Builder()
            .setIndex(53)
            .setMaterial(Material.PAPER)
            .setDisplayName(translation.menu.menuNextPage.let(::toComponent))
            .setOnClickListener { (menu as PaginatedMenu).showNextPage() }
            .build()

    val prevButton: InventorySlot
        get() = InventorySlot.Builder()
            .setIndex(45)
            .setMaterial(Material.PAPER)
            .setDisplayName(translation.menu.menuPrevPage.let(::toComponent))
            .setOnClickListener { (menu as PaginatedMenu).showPrevPage() }
            .build()
}
