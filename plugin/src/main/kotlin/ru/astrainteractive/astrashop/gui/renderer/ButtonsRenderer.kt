package ru.astrainteractive.astrashop.gui.renderer

import org.bukkit.Material
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.inventory.InventoryMenu
import ru.astrainteractive.astralibs.menu.inventory.PaginatedInventoryMenu
import ru.astrainteractive.astralibs.menu.inventory.util.PaginatedInventoryMenuExt.showNextPage
import ru.astrainteractive.astralibs.menu.inventory.util.PaginatedInventoryMenuExt.showPrevPage
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setDisplayName
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setIndex
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setMaterial
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setOnClickListener
import ru.astrainteractive.astrashop.core.PluginTranslation

class ButtonsRenderer(
    private val translation: PluginTranslation,
    private val menu: InventoryMenu,
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
            .setOnClickListener { (menu as PaginatedInventoryMenu).showNextPage() }
            .build()

    val prevButton: InventorySlot
        get() = InventorySlot.Builder()
            .setIndex(45)
            .setMaterial(Material.PAPER)
            .setDisplayName(translation.menu.menuPrevPage.let(::toComponent))
            .setOnClickListener { (menu as PaginatedInventoryMenu).showPrevPage() }
            .build()
}
