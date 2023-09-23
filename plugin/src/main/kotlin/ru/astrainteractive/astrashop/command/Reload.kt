package ru.astrainteractive.astrashop.command

import ru.astrainteractive.astralibs.command.registerCommand
import ru.astrainteractive.astrashop.AstraShop
import ru.astrainteractive.astrashop.command.di.CommandModule
import ru.astrainteractive.astrashop.util.PluginPermission
import ru.astrainteractive.klibs.kdi.getValue

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
    module: CommandModule
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
