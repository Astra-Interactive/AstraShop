package ru.astrainteractive.astrashop.core

import ru.astrainteractive.astralibs.permission.Permission

sealed class PluginPermission(override val value: String) : Permission {
    data object Reload : PluginPermission("astrashop.reload")
    data object EditShop : PluginPermission("astrashop.edit_shop")
    data object QuickSell : PluginPermission("astrashop.quick_sell")
    data object UseShop : PluginPermission("astrashop.use")
}
