package ru.astrainteractive.astrashop.commands

import ru.astrainteractive.astrashop.AstraShop
import ru.astrainteractive.astrashop.modules.TranslationModule
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astrashop.utils.Permission

/**
 * Reload command handler
 */

/**
 * This function called only when atempreload being called
 *
 * Here you should also check for permission
 */
fun CommandManager.reload() = AstraShop.instance.registerCommand("atempreload") {     val translation = TranslationModule.value
    if (!Permission.Reload.hasPermission(sender)) {
        sender.sendMessage(translation.noPermission)
        return@registerCommand
    }
    sender.sendMessage(translation.reload)
    AstraShop.instance.reloadPlugin()
    sender.sendMessage(translation.reloadComplete)
}






