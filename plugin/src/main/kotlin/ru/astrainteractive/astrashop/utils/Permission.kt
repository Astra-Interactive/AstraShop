package ru.astrainteractive.astrashop.utils

sealed class Permission(override val value: String) : IPermission {
    object Reload : Permission("astra_template.reload")
    object EditShop : Permission("astra_template.edit_shop")
}

