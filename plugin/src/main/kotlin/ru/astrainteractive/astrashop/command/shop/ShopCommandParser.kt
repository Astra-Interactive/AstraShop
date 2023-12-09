package ru.astrainteractive.astrashop.command.shop

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.command.api.CommandParser
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible
import ru.astrainteractive.astrashop.command.shop.ShopCommand.Output
import ru.astrainteractive.astrashop.core.PluginPermission

class ShopCommandParser : CommandParser<Output> {
    override fun parse(args: Array<out String>, sender: CommandSender): Output {
        if (sender !is Player) return Output.NotPlayer
        if (!sender.toPermissible().hasPermission(PluginPermission.UseShop)) {
            return Output.NoPermission
        }
        if (args.isEmpty()) return Output.OpenShops(sender)
        if (args.getOrNull(0) == "qs") {
            if (!sender.toPermissible().hasPermission(PluginPermission.QuickSell)) {
                return Output.NoPermission
            }
            return Output.OpenQuickSell(sender)
        }
        return Output.WrongUsage
    }
}
