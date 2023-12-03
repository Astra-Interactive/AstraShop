package ru.astrainteractive.astrashop.gui.quicksell.ui

import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.clicker.MenuClickListener
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.menu.menu.Menu
import ru.astrainteractive.astralibs.menu.menu.MenuSize
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astrashop.core.PluginTranslation
import ru.astrainteractive.astrashop.gui.model.ShopPlayerHolder
import ru.astrainteractive.astrashop.gui.quicksell.presentation.DefaultQuickSellComponent
import ru.astrainteractive.astrashop.gui.quicksell.presentation.QuickSellComponent

class QuickSellGUI(
    override val playerHolder: ShopPlayerHolder,
    private val translation: PluginTranslation,
    private val controller: DefaultQuickSellComponent,
    translationContext: BukkitTranslationContext
) : Menu(), BukkitTranslationContext by translationContext {
    private val clickListener = MenuClickListener()

    override val menuSize: MenuSize = MenuSize.XXS
    override var menuTitle: Component = translation.menu.quickSellTitle.toComponent()

    private val backButton = InventorySlot.Builder {
        index = 8
        itemStack = ItemStack(Material.PAPER).apply {
            editMeta { it.displayName(translation.buttons.buttonBack.toComponent()) }
        }
        click = Click {
            inventory.close()
        }
    }

    override fun onCreated() {
        backButton.setInventorySlot()
        controller.labels
            .onEach {
                when (it) {
                    is QuickSellComponent.Label.Message -> {
                        playerHolder.player.sendMessage(it.desc)
                    }
                }
            }.launchIn(componentScope)
    }

    override fun onInventoryClicked(e: InventoryClickEvent) {
        e.isCancelled = true
        clickListener.onClick(e)
        controller.onItemClicked(
            itemStack = e.currentItem ?: return,
            player = e.whoClicked as? Player ?: return,
            isShiftClick = e.isShiftClick
        )
    }

    override fun onInventoryClose(it: InventoryCloseEvent) {
        controller.close()
        close()
    }
}
