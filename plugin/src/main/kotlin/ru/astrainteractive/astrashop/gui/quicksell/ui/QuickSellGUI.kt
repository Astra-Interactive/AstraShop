package ru.astrainteractive.astrashop.gui.quicksell.ui

import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.menu.clicker.MenuClickListener
import ru.astrainteractive.astralibs.menu.menu.Menu
import ru.astrainteractive.astralibs.menu.menu.MenuSize
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astrashop.core.PluginTranslation
import ru.astrainteractive.astrashop.gui.model.ShopPlayerHolder
import ru.astrainteractive.astrashop.gui.quicksell.presentation.QuickSellController
import ru.astrainteractive.astrashop.gui.util.Buttons

class QuickSellGUI(
    override val playerHolder: ShopPlayerHolder,
    private val translation: PluginTranslation,
    private val controller: QuickSellController,
    translationContext: BukkitTranslationContext
) : Menu(), BukkitTranslationContext by translationContext {
    private val buttons = Buttons(
        lifecycleScope = this,
        translation = translation,
        translationContext = translationContext,
        menu = this
    )

    private val clickListener = MenuClickListener()

    override val menuSize: MenuSize = MenuSize.XXS
    override var menuTitle: Component = translation.menu.quickSellTitle.toComponent()

    private val backButton = buttons.button(
        index = 8,
        item = ItemStack(Material.PAPER).apply {
            editMeta { it.displayName(translation.buttons.buttonBack.toComponent()) }
        },
        onClick = {
            inventory.close()
        }
    )

    override fun onCreated() {
        backButton.setInventorySlot()
        controller.messageChannel.receiveAsFlow()
            .onEach { playerHolder.player.sendMessage(it) }
            .launchIn(componentScope)
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
