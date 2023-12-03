package ru.astrainteractive.astrashop.gui.quicksell.ui

import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.menu.menu.Menu
import ru.astrainteractive.astralibs.menu.menu.MenuSize
import ru.astrainteractive.astralibs.menu.menu.setDisplayName
import ru.astrainteractive.astralibs.menu.menu.setIndex
import ru.astrainteractive.astralibs.menu.menu.setMaterial
import ru.astrainteractive.astralibs.menu.menu.setOnClickListener
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astrashop.core.PluginTranslation
import ru.astrainteractive.astrashop.gui.model.ShopPlayerHolder
import ru.astrainteractive.astrashop.gui.quicksell.presentation.DefaultQuickSellComponent
import ru.astrainteractive.astrashop.gui.quicksell.presentation.QuickSellComponent

class QuickSellGUI(
    override val playerHolder: ShopPlayerHolder,
    private val translation: PluginTranslation,
    private val quickSellComponent: DefaultQuickSellComponent,
    translationContext: BukkitTranslationContext
) : Menu(), BukkitTranslationContext by translationContext {
    override val menuSize: MenuSize = MenuSize.XXS
    override var menuTitle: Component = translation.menu.quickSellTitle.toComponent()

    private val backButton = InventorySlot.Builder()
        .setIndex(8)
        .setMaterial(Material.PAPER)
        .setDisplayName(translation.buttons.buttonBack.toComponent())
        .setOnClickListener {
            inventory.close()
        }.build()

    override fun onCreated() {
        render()
        quickSellComponent.labels
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
        quickSellComponent.onItemClicked(
            itemStack = e.currentItem ?: return,
            player = e.whoClicked as? Player ?: return,
            isShiftClick = e.isShiftClick
        )
    }

    override fun onInventoryClose(it: InventoryCloseEvent) {
        quickSellComponent.close()
        close()
    }

    override fun render() {
        super.render()
        backButton.setInventorySlot()
    }
}
