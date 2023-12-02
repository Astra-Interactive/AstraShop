package ru.astrainteractive.astrashop.command.shop

import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.command.registerCommand
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible
import ru.astrainteractive.astrashop.command.CommandManager
import ru.astrainteractive.astrashop.core.PluginPermission

internal fun CommandManager.shop() = plugin.registerCommand("ashop") {
    if (args.isEmpty()) {
        (sender as? Player)?.let {
            if (!sender.toPermissible().hasPermission(PluginPermission.UseShop)) {
                sender.sendMessage(translation.general.noPermission)
                return@registerCommand
            }
            scope.launch(dispatchers.BukkitAsync) {
                TODO()
//                ShopsGUI(ShopPlayerHolder(it)).openOnMainThread()
            }
        }
    }
    argument(0) { it }.onSuccess {
        if (!sender.toPermissible().hasPermission(PluginPermission.UseShop)) {
            sender.sendMessage(translation.general.noPermission)
            return@onSuccess
        }

        if (it.value == "qs") {
            if (!sender.toPermissible().hasPermission(PluginPermission.QuickSell)) {
                sender.sendMessage(translation.general.noPermission)
                return@onSuccess
            }
            scope.launch(dispatchers.BukkitAsync) {
                if (sender !is Player) return@launch
                TODO()
//                QuickSellGUI(ShopPlayerHolder(sender as Player)).openOnMainThread()
            }
        } else {
            sender.sendMessage("Открытие по названию еще не сделано :(")
        }
    }
}
