package ru.astrainteractive.astrashop.gui.quicksell.presentation

import kotlinx.coroutines.flow.Flow
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.string.StringDesc

interface QuickSellComponent {
    val labels: Flow<Label>

    sealed interface Label {
        class Message(val desc: StringDesc.Raw) : Label
    }

    fun onItemClicked(itemStack: ItemStack, player: Player, isShiftClick: Boolean)
}
