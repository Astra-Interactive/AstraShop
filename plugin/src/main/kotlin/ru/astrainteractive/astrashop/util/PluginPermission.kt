package ru.astrainteractive.astrashop.util

import ru.astrainteractive.astralibs.util.Permission

sealed class PluginPermission(override val value: String) : Permission {
    data object Reload : PluginPermission("astra_template.reload")
    data object EditShop : PluginPermission("astra_template.edit_shop")
    data object QuickSell : PluginPermission("astra_template.quick_sell")
    data object UseShop : PluginPermission("astra_template.shop_use")
}
