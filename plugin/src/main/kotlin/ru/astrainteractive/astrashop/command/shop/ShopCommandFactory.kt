package ru.astrainteractive.astrashop.command.shop

import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.command.api.Command
import ru.astrainteractive.astralibs.command.api.DefaultCommandFactory
import ru.astrainteractive.astralibs.command.registerTabCompleter
import ru.astrainteractive.astralibs.util.withEntry
import ru.astrainteractive.astrashop.command.di.CommandManagerDependencies
import ru.astrainteractive.klibs.kdi.Factory

class ShopCommandFactory(
    private val dependencies: CommandManagerDependencies,
    private val plugin: JavaPlugin
) : Factory<ShopCommand> {
    private fun tabCompleter() = plugin.registerTabCompleter("ashop") {
        when {
            args.size == 1 -> listOf("qs").withEntry(args.getOrNull(0))
            else -> emptyList()
        }
    }

    private inner class ShopCommandImpl :
        ShopCommand,
        Command<ShopCommand.Output, ShopCommand.Output> by DefaultCommandFactory.create(
            alias = "ashop",
            commandParser = ShopCommandParser(),
            commandExecutor = ShopCommandExecutor(dependencies),
            resultHandler = { commandSender, result ->
                when (result) {
                    ShopCommand.Output.NoPermission -> with(dependencies.translationContext) {
                        commandSender.sendMessage(dependencies.translation.general.noPermission)
                    }

                    ShopCommand.Output.NotPlayer -> with(dependencies.translationContext) {
                        commandSender.sendMessage(dependencies.translation.general.notPlayer)
                    }

                    ShopCommand.Output.WrongUsage -> with(dependencies.translationContext) {
                        commandSender.sendMessage(dependencies.translation.general.wrongUsage)
                    }

                    else -> Unit
                }
            },
            mapper = Command.Mapper.NoOp()
        )

    override fun create(): ShopCommand {
        tabCompleter()
        return ShopCommandImpl().also {
            it.register(plugin)
        }
    }
}
