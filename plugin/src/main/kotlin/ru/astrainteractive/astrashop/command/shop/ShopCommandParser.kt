package ru.astrainteractive.astrashop.command.shop

import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContext
import ru.astrainteractive.astralibs.command.api.parser.BukkitCommandParser
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible
import ru.astrainteractive.astrashop.command.shop.ShopCommand.Output
import ru.astrainteractive.astrashop.core.PluginPermission

class ShopCommandParser : BukkitCommandParser<Output> {
    override fun parse(commandContext: BukkitCommandContext): Output {
        val sender = commandContext.sender
        val args = commandContext.args
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
