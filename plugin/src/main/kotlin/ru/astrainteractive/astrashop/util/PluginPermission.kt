package ru.astrainteractive.astrashop.util

import ru.astrainteractive.astralibs.utils.Permission

sealed class PluginPermission(override val value: String) : Permission {
    object Reload : PluginPermission("astra_template.reload")
    object EditShop : PluginPermission("astra_template.edit_shop")
    object QuickSell : PluginPermission("astra_template.quick_sell")
    object UseShop : PluginPermission("astra_template.shop_use")
}
