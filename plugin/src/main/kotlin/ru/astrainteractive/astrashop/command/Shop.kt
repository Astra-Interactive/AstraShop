package ru.astrainteractive.astrashop.command

import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.command.registerCommand
import ru.astrainteractive.astrashop.AstraShop
import ru.astrainteractive.astrashop.command.di.CommandContainer
import ru.astrainteractive.astrashop.gui.util.ShopPlayerHolder
import ru.astrainteractive.astrashop.util.PluginPermission
import ru.astrainteractive.astrashop.util.QuickSellGuiRoute
import ru.astrainteractive.astrashop.util.ShopsGuiRoute
import ru.astrainteractive.klibs.kdi.getValue

internal fun CommandManager.shop(
    plugin: AstraShop,
    module: CommandContainer
) = plugin.registerCommand("ashop") {
    val translation by module.translation
    val scope by module.scope
    val dispatchers by module.dispatchers
    val router by module.router

    if (args.isEmpty()) {
        (sender as? Player)?.let { player ->
            if (!PluginPermission.UseShop.hasPermission(sender)) {
                sender.sendMessage(translation.noPermission)
                return@registerCommand
            }
            val playerHolder = ShopPlayerHolder(player)
            router.open(ShopsGuiRoute(playerHolder))
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
                val playerHolder = ShopPlayerHolder(sender as Player)
                router.open(QuickSellGuiRoute(playerHolder))
            }
        } else {
            sender.sendMessage("Открытие по названию еще не сделано :(")
        }
    }
}
