package ru.astrainteractive.astrashop.commands

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.commands.DSLCommand
import ru.astrainteractive.astrashop.gui.PlayerHolder
import ru.astrainteractive.astrashop.gui.shops.ShopsGUI

fun CommandManager.shop() = DSLCommand("shop") {
    if (args.isEmpty()) (sender as? Player)?.let {
        PluginScope.launch(Dispatchers.IO) {
            ShopsGUI(PlayerHolder(it)).open()
        }
    }
    argument(
        index = 0,
        parser = { it },
        onResult = {
            sender.sendMessage("Открытие по названию еще не сделано :(")
        }
    )
}