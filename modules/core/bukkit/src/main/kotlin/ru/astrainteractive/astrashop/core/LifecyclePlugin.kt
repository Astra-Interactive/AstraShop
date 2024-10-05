package ru.astrainteractive.astrashop.core

import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

abstract class LifecyclePlugin : JavaPlugin(), Lifecycle {
    override fun onReload() {
        super<Lifecycle>.onReload()
    }

    override fun onEnable() {
        super<Lifecycle>.onEnable()
        super<JavaPlugin>.onEnable()
    }

    override fun onDisable() {
        super<Lifecycle>.onDisable()
        super<JavaPlugin>.onDisable()
    }
}
