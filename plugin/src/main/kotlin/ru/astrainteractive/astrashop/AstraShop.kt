package ru.astrainteractive.astrashop

import kotlinx.coroutines.cancel
import org.bukkit.Bukkit
import ru.astrainteractive.astrashop.commands.CommandManager
import ru.astrainteractive.astrashop.modules.TranslationModule
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.events.GlobalEventManager
import ru.astrainteractive.astralibs.menu.SharedInventoryClickEvent
import ru.astrainteractive.astralibs.utils.setupWithSpigot
import ru.astrainteractive.astrashop.utils.Files

/**
 * Initial class for your plugin
 */
class AstraShop : JavaPlugin() {
    companion object {
        lateinit var instance: AstraShop
    }

    init {
        instance = this
    }

    /**
     * This method called when server starts or PlugMan load plugin.
     */
    override fun onEnable() {
        AstraLibs.rememberPlugin(this)
        Logger.setupWithSpigot("AstraShop")
        CommandManager.enable()
        SharedInventoryClickEvent.onEnable(GlobalEventManager)
    }

    /**
     * This method called when server is shutting down or when PlugMan disable plugin.
     */
    override fun onDisable() {
        HandlerList.unregisterAll(this)
        GlobalEventManager.onDisable()
        PluginScope.cancel()
        Bukkit.getOnlinePlayers().forEach {
            it.closeInventory()
        }
    }

    /**
     * As it says, function for plugin reload
     */
    fun reloadPlugin() {
        Files.configFile.reload()
        TranslationModule.reload()
        Bukkit.getOnlinePlayers().forEach {
            it.closeInventory()
        }
    }

}


