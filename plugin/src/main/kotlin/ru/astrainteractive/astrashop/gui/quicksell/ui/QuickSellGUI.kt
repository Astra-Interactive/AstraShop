package ru.astrainteractive.astrashop.gui.quicksell.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.menu.menu.Menu
import ru.astrainteractive.astralibs.menu.menu.MenuSize
import ru.astrainteractive.astralibs.menu.menu.setDisplayName
import ru.astrainteractive.astralibs.menu.menu.setIndex
import ru.astrainteractive.astralibs.menu.menu.setMaterial
import ru.astrainteractive.astralibs.menu.menu.setOnClickListener
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astrashop.core.PluginTranslation
import ru.astrainteractive.astrashop.gui.model.ShopPlayerHolder
import ru.astrainteractive.astrashop.gui.quicksell.presentation.DefaultQuickSellComponent
import ru.astrainteractive.astrashop.gui.quicksell.presentation.QuickSellComponent

class QuickSellGUI(
    override val playerHolder: ShopPlayerHolder,
    private val translation: PluginTranslation,
    private val quickSellComponent: DefaultQuickSellComponent,
    kyoriComponentSerializer: KyoriComponentSerializer
) : Menu(), KyoriComponentSerializer by kyoriComponentSerializer {
    override val childComponents: List<CoroutineScope> = listOf(quickSellComponent)
    override val menuSize: MenuSize = MenuSize.XXS
    override var menuTitle: Component = translation.menu.quickSellTitle.let(::toComponent)

    private val backButton = InventorySlot.Builder()
        .setIndex(8)
        .setMaterial(Material.PAPER)
        .setDisplayName(translation.buttons.buttonBack.let(::toComponent))
        .setOnClickListener {
            inventory.close()
        }.build()

    override fun onCreated() {
        render()
        quickSellComponent.labels
            .onEach {
                when (it) {
                    is QuickSellComponent.Label.Message -> {
                        toComponent(it.desc).run(playerHolder.player::sendMessage)
                    }
                }
            }.launchIn(menuScope)
    }

    override fun onInventoryClicked(e: InventoryClickEvent) {
        super.onInventoryClicked(e)
        e.isCancelled = true
        quickSellComponent.onItemClicked(
            itemStack = e.currentItem ?: return,
            player = e.whoClicked as? Player ?: return,
            isShiftClick = e.isShiftClick
        )
    }

    override fun render() {
        super.render()
        backButton.setInventorySlot()
    }
}
