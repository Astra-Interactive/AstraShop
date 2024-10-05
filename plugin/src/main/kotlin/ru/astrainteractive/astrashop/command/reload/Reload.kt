package ru.astrainteractive.astrashop.command.reload

import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible
import ru.astrainteractive.astrashop.command.CommandManager
import ru.astrainteractive.astrashop.core.PluginPermission

/**
 * This function called only when ashopreload being called
 *
 * Here you should also check for permission
 */
internal fun CommandManager.reload() = plugin.getCommand("ashopreload")?.setExecutor { sender, command, label, args ->
    if (!sender.toPermissible().hasPermission(PluginPermission.Reload)) {
        kyoriComponentSerializer
            .toComponent(translation.general.noPermission)
            .run(sender::sendMessage)
        return@setExecutor true
    }
    kyoriComponentSerializer
        .toComponent(translation.general.reload)
        .run(sender::sendMessage)
    plugin.onReload()
    kyoriComponentSerializer
        .toComponent(translation.general.reloadComplete)
        .run(sender::sendMessage)
    true
}
