package ru.astrainteractive.astrashop.command.shop

import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContext
import ru.astrainteractive.astralibs.command.api.error.ErrorHandler
import ru.astrainteractive.astralibs.command.api.exception.DefaultCommandException
import ru.astrainteractive.astrashop.command.di.CommandManagerDependencies

internal class ShopCommandErrorHandler(
    dependencies: CommandManagerDependencies
) : ErrorHandler<BukkitCommandContext>,
    CommandManagerDependencies by dependencies {
    override fun handle(commandContext: BukkitCommandContext, throwable: Throwable) {
        val commandSender = commandContext.sender
        when (throwable) {
            is DefaultCommandException.NoPermissionException ->
                kyoriComponentSerializer
                    .toComponent(translation.general.noPermission)
                    .run(commandSender::sendMessage)

            is ShopCommand.Error.NotPlayer ->
                kyoriComponentSerializer
                    .toComponent(translation.general.notPlayer)
                    .run(commandSender::sendMessage)

            is ShopCommand.Error.WrongUsage ->
                kyoriComponentSerializer
                    .toComponent(translation.general.wrongUsage)
                    .run(commandSender::sendMessage)

            else -> error("Unhandled error $throwable::class")
        }
    }
}
