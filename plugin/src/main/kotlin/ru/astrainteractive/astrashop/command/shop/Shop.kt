package ru.astrainteractive.astrashop.command.shop

import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.command.registerCommand
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible
import ru.astrainteractive.astrashop.command.CommandManager
import ru.astrainteractive.astrashop.core.PluginPermission
import ru.astrainteractive.astrashop.gui.model.ShopPlayerHolder
import ru.astrainteractive.astrashop.gui.router.GuiRouter

internal fun CommandManager.shop() = plugin.registerCommand("ashop") {
    if (args.isEmpty()) {
        (sender as? Player)?.let {
            if (!sender.toPermissible().hasPermission(PluginPermission.UseShop)) {
                sender.sendMessage(translation.general.noPermission)
                return@registerCommand
            }
            scope.launch(dispatchers.BukkitAsync) {
                val route = GuiRouter.Route.Shops(ShopPlayerHolder(it))
                router.open(route)
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
                val route = GuiRouter.Route.QuickSell(sender as Player)
                router.open(route)
            }
        } else {
            sender.sendMessage("Открытие по названию еще не сделано :(")
        }
    }
}
