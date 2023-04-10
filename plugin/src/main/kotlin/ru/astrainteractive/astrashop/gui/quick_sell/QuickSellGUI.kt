package ru.astrainteractive.astrashop.gui.quick_sell

import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.menu.*
import ru.astrainteractive.astralibs.menu.menu.Menu
import ru.astrainteractive.astralibs.menu.utils.MenuSize
import ru.astrainteractive.astralibs.menu.utils.click.MenuClickListener
import ru.astrainteractive.astrashop.gui.*
import ru.astrainteractive.astrashop.gui.ShopPlayerHolder
import ru.astrainteractive.astrashop.modules.TranslationModule


class QuickSellGUI(override val playerHolder: ShopPlayerHolder) : Menu() {

    private val controller = QuickSellController()
    private val translation by TranslationModule
    private val clickListener = MenuClickListener()

    override val menuSize: MenuSize = MenuSize.XXS
    override var menuTitle: String = translation.quickSellTitle


    private val backButton = button(
        index = 8,
        item = ItemStack(Material.PAPER).apply {
            editMeta { it.setDisplayName(translation.buttonBack) }
        },
        onClick = {
            inventory.close()
        })


    override fun onCreated() {
        backButton.setInventoryButton()
    }

    override fun onInventoryClicked(e: InventoryClickEvent) {
        e.isCancelled = true
        clickListener.onClick(e)
        controller.onItemClicked(e)
    }

    override fun onInventoryClose(it: InventoryCloseEvent) {
        controller.close()
        close()
    }

}