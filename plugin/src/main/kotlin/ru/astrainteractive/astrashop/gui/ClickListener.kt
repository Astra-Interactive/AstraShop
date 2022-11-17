package ru.astrainteractive.astrashop.gui

import org.bukkit.event.inventory.InventoryClickEvent
import ru.astrainteractive.astralibs.menu.IInventoryButton

class ClickListener {
    private var clicks: HashMap<Int, (InventoryClickEvent) -> Unit> = HashMap()

    fun remember(button: IInventoryButton) {
        clicks[button.index] = button.onClick
    }

    fun clear() {
        clicks.clear()
    }
    fun handle(e:InventoryClickEvent){
        clicks[e.slot]?.invoke(e)
    }
}