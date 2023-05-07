package ru.astrainteractive.astrashop.commands

import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.getValue
import ru.astrainteractive.astrashop.AstraShop
import ru.astrainteractive.astrashop.commands.di.CommandsModule
import ru.astrainteractive.astrashop.utils.PluginPermission

/**
 * Reload command handler
 */

/**
 * This function called only when atempreload being called
 *
 * Here you should also check for permission
 */
internal fun CommandManager.reload(
    plugin: AstraShop,
    module: CommandsModule
) = plugin.registerCommand("atempreload") {
    val translation by module.translation
    if (!PluginPermission.Reload.hasPermission(sender)) {
        sender.sendMessage(translation.noPermission)
        return@registerCommand
    }
    sender.sendMessage(translation.reload)
    plugin.reloadPlugin()
    sender.sendMessage(translation.reloadComplete)
}
