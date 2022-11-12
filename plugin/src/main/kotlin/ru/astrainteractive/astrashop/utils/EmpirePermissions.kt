package ru.astrainteractive.astrashop.utils

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

sealed class AstraPermission(val value: String) {
    object Reload : AstraPermission("astra_template.reload")
    object EditShop:AstraPermission("astra_template.edit_shop")
    fun hasPermission(player: CommandSender) = player.hasPermission(value)

    /**
     * If has astra_template.damage.2 => returns 2
     */
    fun permissionSize(player: Player) = player.effectivePermissions
        .firstOrNull { it.permission.startsWith(value) }
        ?.permission
        ?.replace("$value.", "")
        ?.toIntOrNull()
}