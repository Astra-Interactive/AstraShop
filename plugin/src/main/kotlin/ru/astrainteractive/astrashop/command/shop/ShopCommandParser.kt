package ru.astrainteractive.astrashop.command.shop

import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContext
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContextExt.requirePermission
import ru.astrainteractive.astralibs.command.api.parser.CommandParser
import ru.astrainteractive.astrashop.command.shop.ShopCommand.Output
import ru.astrainteractive.astrashop.core.PluginPermission

class ShopCommandParser : CommandParser<Output, BukkitCommandContext> {
    override fun parse(commandContext: BukkitCommandContext): Output {
        val sender = commandContext.sender
        val args = commandContext.args
        if (sender !is Player) throw ShopCommand.Error.NotPlayer
        commandContext.requirePermission(PluginPermission.UseShop)
        if (args.isEmpty()) return Output.OpenShops(sender)
        if (args.getOrNull(0) == "qs") {
            commandContext.requirePermission(PluginPermission.QuickSell)
            return Output.OpenQuickSell(sender)
        }
        throw ShopCommand.Error.WrongUsage
    }
}
