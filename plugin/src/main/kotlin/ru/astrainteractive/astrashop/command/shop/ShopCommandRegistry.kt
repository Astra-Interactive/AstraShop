package ru.astrainteractive.astrashop.command.shop

import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.command.api.util.PluginExt.registerCommand
import ru.astrainteractive.astralibs.util.StringListExt.withEntry
import ru.astrainteractive.astrashop.command.di.CommandManagerDependencies

class ShopCommandRegistry(
    private val dependencies: CommandManagerDependencies,
    private val plugin: JavaPlugin
) {
    private fun tabCompleter() = plugin.getCommand("ashop")?.setTabCompleter { sender, command, label, args ->
        when {
            args.size == 1 -> listOf("qs").withEntry(args.getOrNull(0))
            else -> emptyList()
        }
    }

    fun register() {
        tabCompleter()
        plugin.registerCommand(
            alias = "ashop",
            commandParser = ShopCommandParser(),
            commandExecutor = ShopCommandExecutor(dependencies),
            errorHandler = ShopCommandErrorHandler(dependencies),
        )
    }
}
