package ru.astrainteractive.astrashop.command.reload

import ru.astrainteractive.astralibs.command.registerCommand
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible
import ru.astrainteractive.astrashop.command.CommandManager
import ru.astrainteractive.astrashop.core.PluginPermission

/**
 * This function called only when ashopreload being called
 *
 * Here you should also check for permission
 */
internal fun CommandManager.reload() = plugin.registerCommand("ashopreload") {
    if (!sender.toPermissible().hasPermission(PluginPermission.Reload)) {
        kyoriComponentSerializer
            .toComponent(translation.general.noPermission)
            .run(sender::sendMessage)
        return@registerCommand
    }
    kyoriComponentSerializer
        .toComponent(translation.general.reload)
        .run(sender::sendMessage)
    plugin.reloadPlugin()
    kyoriComponentSerializer
        .toComponent(translation.general.reloadComplete)
        .run(sender::sendMessage)
}
