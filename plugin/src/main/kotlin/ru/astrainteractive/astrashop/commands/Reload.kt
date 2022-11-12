package ru.astrainteractive.astrashop.commands

import CommandManager
import ru.astrainteractive.astrashop.AstraShop
import ru.astrainteractive.astrashop.modules.TranslationModule
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.utils.registerCommand
import ru.astrainteractive.astrashop.utils.AstraPermission

/**
 * Reload command handler
 */

/**
 * This function called only when atempreload being called
 *
 * Here you should also check for permission
 */
fun CommandManager.reload() = AstraLibs.registerCommand("atempreload") { sender, args ->
    val translation = TranslationModule.value
    if (!AstraPermission.Reload.hasPermission(sender)) {
        sender.sendMessage(translation.noPermission)
        return@registerCommand
    }
    sender.sendMessage(translation.reload)
    AstraShop.instance.reloadPlugin()
    sender.sendMessage(translation.reloadComplete)
}






