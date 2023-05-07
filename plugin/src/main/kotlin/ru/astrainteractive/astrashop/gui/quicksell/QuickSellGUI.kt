package ru.astrainteractive.astrashop.gui.quicksell

import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.getValue
import ru.astrainteractive.astralibs.menu.clicker.MenuClickListener
import ru.astrainteractive.astralibs.menu.menu.Menu
import ru.astrainteractive.astralibs.menu.menu.MenuSize
import ru.astrainteractive.astrashop.di.impl.RootModuleImpl
import ru.astrainteractive.astrashop.gui.ShopPlayerHolder
import ru.astrainteractive.astrashop.gui.button

class QuickSellGUI(override val playerHolder: ShopPlayerHolder) : Menu() {

    private val translation by RootModuleImpl.translation

    private val controller = QuickSellController()
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
        }
    )

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
