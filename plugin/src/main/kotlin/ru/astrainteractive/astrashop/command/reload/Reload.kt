package ru.astrainteractive.astrashop.command.reload

import ru.astrainteractive.astralibs.command.registerCommand
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible
import ru.astrainteractive.astrashop.command.CommandManager
import ru.astrainteractive.astrashop.core.PluginPermission
import ru.astrainteractive.klibs.kdi.getValue

/**
 * Reload command handler
 */

/**
 * This function called only when atempreload being called
 *
 * Here you should also check for permission
 */
internal fun CommandManager.reload() = plugin.registerCommand("atempreload") {
    if (!sender.toPermissible().hasPermission(PluginPermission.Reload)) {
        sender.sendMessage(translation.general.noPermission)
        return@registerCommand
    }
    sender.sendMessage(translation.general.reload)
    plugin.reloadPlugin()
    sender.sendMessage(translation.general.reloadComplete)
}
