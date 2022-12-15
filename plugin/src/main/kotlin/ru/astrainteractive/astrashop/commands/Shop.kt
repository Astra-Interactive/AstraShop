package ru.astrainteractive.astrashop.commands

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.commands.DSLCommand
import ru.astrainteractive.astrashop.gui.PlayerHolder
import ru.astrainteractive.astrashop.gui.quick_sell.QuickSellGUI
import ru.astrainteractive.astrashop.gui.shops.ShopsGUI

fun CommandManager.shop() = DSLCommand("ashop") {
    if (args.isEmpty()) (sender as? Player)?.let {
        PluginScope.launch(Dispatchers.IO) {
            ShopsGUI(PlayerHolder(it)).open()
        }
    }
    argument(
        index = 0,
        parser = { it },
        onResult = {
            if (it.value == "qs") {
                PluginScope.launch(Dispatchers.IO) {
                    if (sender !is Player) return@launch
                    QuickSellGUI(PlayerHolder(sender as Player)).open()
                }
            } else sender.sendMessage("Открытие по названию еще не сделано :(")
        }
    )

}