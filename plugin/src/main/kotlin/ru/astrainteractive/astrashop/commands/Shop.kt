package ru.astrainteractive.astrashop.commands

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astrashop.gui.PlayerHolder
import ru.astrainteractive.astrashop.gui.quick_sell.QuickSellGUI
import ru.astrainteractive.astrashop.gui.shops.ShopsGUI
import ru.astrainteractive.astrashop.modules.TranslationModule
import ru.astrainteractive.astrashop.utils.Permission

fun CommandManager.shop() = AstraLibs.instance.registerCommand("ashop") {
    val translation by TranslationModule
    if (args.isEmpty()) (sender as? Player)?.let {
        if (!Permission.UseShop.hasPermission(sender)){
            sender.sendMessage(translation.noPermission)
            return@registerCommand
        }
        PluginScope.launch(Dispatchers.IO) {
            ShopsGUI(PlayerHolder(it)).open()
        }
    }
    argument(
        index = 0,
        parser = { it },
        onResult = {
            if (!Permission.UseShop.hasPermission(sender)){
                sender.sendMessage(translation.noPermission)
                return@argument
            }

            if (it.value == "qs") {
                if (!Permission.QuickSell.hasPermission(sender)){
                    sender.sendMessage(translation.noPermission)
                    return@argument
                }
                PluginScope.launch(Dispatchers.IO) {
                    if (sender !is Player) return@launch
                    QuickSellGUI(PlayerHolder(sender as Player)).open()
                }
            } else sender.sendMessage("Открытие по названию еще не сделано :(")
        }
    )

}