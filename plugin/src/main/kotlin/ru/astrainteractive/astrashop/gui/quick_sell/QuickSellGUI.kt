package ru.astrainteractive.astrashop.gui.quick_sell

import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.events.DSLEvent
import ru.astrainteractive.astralibs.menu.*
import ru.astrainteractive.astrashop.asState
import ru.astrainteractive.astrashop.domain.models.ShopConfig
import ru.astrainteractive.astrashop.gui.*
import ru.astrainteractive.astrashop.gui.PlayerHolder
import ru.astrainteractive.astrashop.gui.shop.state.ShopIntent
import ru.astrainteractive.astrashop.modules.TranslationModule
import ru.astrainteractive.astrashop.utils.copy
import ru.astrainteractive.astrashop.utils.toItemStack
import ru.astrainteractive.astrashop.utils.withMeta
import kotlin.math.pow


class QuickSellGUI(override val playerMenuUtility: PlayerHolder) : Menu() {

    private val controller = QuickSellController()
    private val translation by TranslationModule
    private val clickListener = ClickListener()

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
        backButton.set(inventory)
    }

    override fun onInventoryClicked(e: InventoryClickEvent) {
        e.isCancelled = true
        clickListener.handle(e)
        controller.onItemClicked(e)
    }

    override fun onInventoryClose(it: InventoryCloseEvent) {
        controller.close()
        close()
    }

}