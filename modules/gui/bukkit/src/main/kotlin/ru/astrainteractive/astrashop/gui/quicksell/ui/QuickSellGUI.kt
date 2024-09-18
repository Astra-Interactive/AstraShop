package ru.astrainteractive.astrashop.gui.quicksell.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.menu.inventory.InventoryMenu
import ru.astrainteractive.astralibs.menu.inventory.model.InventorySize
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setDisplayName
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setIndex
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setMaterial
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setOnClickListener
import ru.astrainteractive.astrashop.core.PluginTranslation
import ru.astrainteractive.astrashop.gui.model.BukkitItemStack
import ru.astrainteractive.astrashop.gui.model.BukkitShopPlayerHolder
import ru.astrainteractive.astrashop.gui.quicksell.presentation.QuickSellComponent

internal class QuickSellGUI(
    override val playerHolder: BukkitShopPlayerHolder,
    private val translation: PluginTranslation,
    private val quickSellComponent: QuickSellComponent,
    kyoriComponentSerializer: KyoriComponentSerializer
) : InventoryMenu(), KyoriComponentSerializer by kyoriComponentSerializer {
    override val childComponents: List<CoroutineScope> = listOf(quickSellComponent)
    override val inventorySize: InventorySize = InventorySize.XXS
    override var title: Component = translation.menu.quickSellTitle.let(::toComponent)

    private val backButton = InventorySlot.Builder()
        .setIndex(8)
        .setMaterial(Material.PAPER)
        .setDisplayName(translation.buttons.buttonBack.let(::toComponent))
        .setOnClickListener {
            inventory.close()
        }.build()

    override fun onInventoryCreated() {
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
            itemStack = e.currentItem?.let(::BukkitItemStack) ?: return,
            playerUUID = (e.whoClicked as? Player)?.uniqueId ?: return,
            isShiftClick = e.isShiftClick
        )
    }

    override fun render() {
        super.render()
        backButton.setInventorySlot()
    }
}
