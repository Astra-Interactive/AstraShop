package ru.astrainteractive.astrashop.command

import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.command.registerCommand
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible
import ru.astrainteractive.astrashop.AstraShop
import ru.astrainteractive.astrashop.command.di.CommandModule
import ru.astrainteractive.astrashop.gui.ShopPlayerHolder
import ru.astrainteractive.astrashop.gui.quicksell.QuickSellGUI
import ru.astrainteractive.astrashop.gui.shops.ShopsGUI
import ru.astrainteractive.astrashop.util.PluginPermission
import ru.astrainteractive.astrashop.util.openOnMainThread
import ru.astrainteractive.klibs.kdi.getValue

internal fun CommandManager.shop(
    plugin: AstraShop,
    module: CommandModule
) = plugin.registerCommand("ashop") {
    val translation by module.translation
    val scope by module.scope
    val dispatchers by module.dispatchers

    if (args.isEmpty()) {
        (sender as? Player)?.let {
            if (!sender.toPermissible().hasPermission(PluginPermission.UseShop)) {
                sender.sendMessage(translation.noPermission)
                return@registerCommand
            }
            scope.launch(dispatchers.BukkitAsync) {
                ShopsGUI(ShopPlayerHolder(it)).openOnMainThread()
            }
        }
    }
    argument(0) { it }.onSuccess {
        if (!sender.toPermissible().hasPermission(PluginPermission.UseShop)) {
            sender.sendMessage(translation.noPermission)
            return@onSuccess
        }

        if (it.value == "qs") {
            if (!sender.toPermissible().hasPermission(PluginPermission.QuickSell)) {
                sender.sendMessage(translation.noPermission)
                return@onSuccess
            }
            scope.launch(dispatchers.BukkitAsync) {
                if (sender !is Player) return@launch
                QuickSellGUI(ShopPlayerHolder(sender as Player)).openOnMainThread()
            }
        } else {
            sender.sendMessage("Открытие по названию еще не сделано :(")
        }
    }
}
