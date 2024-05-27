package ru.astrainteractive.astrashop.command.shop

import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContext
import ru.astrainteractive.astralibs.command.api.sideeffect.BukkitCommandSideEffect
import ru.astrainteractive.astrashop.command.di.CommandManagerDependencies

internal class ShopCommandSideEffect(
    dependencies: CommandManagerDependencies
) : BukkitCommandSideEffect<ShopCommand.Output>,
    CommandManagerDependencies by dependencies {
    override fun handle(commandContext: BukkitCommandContext, result: ShopCommand.Output) {
        val commandSender = commandContext.sender
        when (result) {
            ShopCommand.Output.NoPermission ->
                kyoriComponentSerializer
                    .toComponent(translation.general.noPermission)
                    .run(commandSender::sendMessage)

            ShopCommand.Output.NotPlayer ->
                kyoriComponentSerializer
                    .toComponent(translation.general.notPlayer)
                    .run(commandSender::sendMessage)

            ShopCommand.Output.WrongUsage ->
                kyoriComponentSerializer
                    .toComponent(translation.general.wrongUsage)
                    .run(commandSender::sendMessage)

            else -> Unit
        }
    }
}
