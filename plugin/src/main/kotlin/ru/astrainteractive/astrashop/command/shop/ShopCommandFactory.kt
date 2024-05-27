package ru.astrainteractive.astrashop.command.shop

import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.command.api.command.Command
import ru.astrainteractive.astralibs.command.api.commandfactory.BukkitCommandFactory
import ru.astrainteractive.astralibs.command.api.registry.BukkitCommandRegistry
import ru.astrainteractive.astralibs.command.api.registry.BukkitCommandRegistryContext.Companion.toCommandRegistryContext
import ru.astrainteractive.astralibs.util.StringListExt.withEntry
import ru.astrainteractive.astrashop.command.di.CommandManagerDependencies

class ShopCommandFactory(
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
        val command = BukkitCommandFactory.create(
            alias = "ashop",
            commandParser = ShopCommandParser(),
            commandExecutor = ShopCommandExecutor(dependencies),
            commandSideEffect = ShopCommandSideEffect(dependencies),
            mapper = Command.Mapper.NoOp()
        )
        BukkitCommandRegistry.register(command, plugin.toCommandRegistryContext())
    }
}
