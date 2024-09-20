package ru.astrainteractive.astrashop.gui.quicksell.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astrashop.gui.model.SharedItemStack
import java.util.UUID

interface QuickSellComponent : CoroutineScope {
    val labels: Flow<Label>

    sealed interface Label {
        class Message(val desc: StringDesc.Raw) : Label
    }

    fun onItemClicked(itemStack: SharedItemStack, playerUUID: UUID, isShiftClick: Boolean)
}
