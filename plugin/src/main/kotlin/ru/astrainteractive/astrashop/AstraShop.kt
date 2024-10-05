package ru.astrainteractive.astrashop

import ru.astrainteractive.astrashop.core.LifecyclePlugin
import ru.astrainteractive.astrashop.di.RootModule

class AstraShop : LifecyclePlugin() {
    private val rootModule: RootModule = RootModule.Default(this)

    override fun onEnable() {
        rootModule.lifecycle.onEnable()
    }

    override fun onDisable() {
        rootModule.lifecycle.onDisable()
    }

    override fun onReload() {
        rootModule.lifecycle.onReload()
    }
}
