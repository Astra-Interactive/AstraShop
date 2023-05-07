package ru.astrainteractive.astrashop.commands

import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.getValue
import ru.astrainteractive.astrashop.AstraShop
import ru.astrainteractive.astrashop.commands.di.CommandsModule
import ru.astrainteractive.astrashop.gui.ShopPlayerHolder
import ru.astrainteractive.astrashop.gui.quicksell.QuickSellGUI
import ru.astrainteractive.astrashop.gui.shops.ShopsGUI
import ru.astrainteractive.astrashop.utils.PluginPermission
import ru.astrainteractive.astrashop.utils.openOnMainThread

internal fun CommandManager.shop(
    plugin: AstraShop,
    module: CommandsModule
) = plugin.registerCommand("ashop") {
    val translation by module.translation
    val scope by module.scope
    val dispatchers by module.dispatchers

    if (args.isEmpty()) {
        (sender as? Player)?.let {
            if (!PluginPermission.UseShop.hasPermission(sender)) {
                sender.sendMessage(translation.noPermission)
                return@registerCommand
            }
            scope.launch(dispatchers.BukkitAsync) {
                ShopsGUI(ShopPlayerHolder(it)).openOnMainThread()
            }
        }
    }
    argument(0) { it }.onSuccess {
        if (!PluginPermission.UseShop.hasPermission(sender)) {
            sender.sendMessage(translation.noPermission)
            return@onSuccess
        }

        if (it.value == "qs") {
            if (!PluginPermission.QuickSell.hasPermission(sender)) {
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
