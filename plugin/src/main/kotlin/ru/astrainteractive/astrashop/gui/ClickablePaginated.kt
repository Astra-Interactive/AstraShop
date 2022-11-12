package ru.astrainteractive.astrashop.gui

import org.bukkit.event.inventory.InventoryClickEvent
import ru.astrainteractive.astralibs.menu.IInventoryButton
import ru.astrainteractive.astralibs.menu.PaginatedMenu

interface IClickablePaginated {
    var clicks: HashMap<Int, (InventoryClickEvent) -> Unit>

    fun rememberClick(button: IInventoryButton) {
        clicks[button.index] = button.onClick
    }

    fun forgetClicks() {
        clicks.clear()
    }
    fun handleClick(e:InventoryClickEvent){
        clicks[e.slot]?.invoke(e)
    }
}