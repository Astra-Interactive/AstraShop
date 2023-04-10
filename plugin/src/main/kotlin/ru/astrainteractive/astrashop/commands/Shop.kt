package ru.astrainteractive.astrashop.commands

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.async.BukkitAsync
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astrashop.AstraShop
import ru.astrainteractive.astrashop.gui.ShopPlayerHolder
import ru.astrainteractive.astrashop.gui.quick_sell.QuickSellGUI
import ru.astrainteractive.astrashop.gui.shops.ShopsGUI
import ru.astrainteractive.astrashop.modules.TranslationModule
import ru.astrainteractive.astrashop.utils.PluginPermission

fun CommandManager.shop() = AstraShop.instance.registerCommand("ashop") {
    val translation by TranslationModule
    if (args.isEmpty()) (sender as? Player)?.let {
        if (!PluginPermission.UseShop.hasPermission(sender)){
            sender.sendMessage(translation.noPermission)
            return@registerCommand
        }
        PluginScope.launch(Dispatchers.BukkitAsync) {
            ShopsGUI(ShopPlayerHolder(it)).open()
        }
    }
    argument(0){it}.onSuccess {
        if (!PluginPermission.UseShop.hasPermission(sender)){
            sender.sendMessage(translation.noPermission)
            return@onSuccess
        }

        if (it.value == "qs") {
            if (!PluginPermission.QuickSell.hasPermission(sender)){
                sender.sendMessage(translation.noPermission)
                return@onSuccess
            }
            PluginScope.launch(Dispatchers.BukkitAsync) {
                if (sender !is Player) return@launch
                QuickSellGUI(ShopPlayerHolder(sender as Player)).open()
            }
        } else sender.sendMessage("Открытие по названию еще не сделано :(")

    }
}