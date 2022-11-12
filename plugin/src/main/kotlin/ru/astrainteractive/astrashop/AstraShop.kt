package ru.astrainteractive.astrashop

import CommandManager
import ru.astrainteractive.astrashop.modules.TranslationModule
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.events.GlobalEventManager
import ru.astrainteractive.astrashop.events.EventHandler
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
     * Class for handling all of your events
     */
    private var eventHandler: EventHandler? = null


    /**
     * This method called when server starts or PlugMan load plugin.
     */
    override fun onEnable() {
        AstraLibs.rememberPlugin(this)
        Logger.prefix = "AstraShop"
        eventHandler = EventHandler()
        CommandManager()
    }

    /**
     * This method called when server is shutting down or when PlugMan disable plugin.
     */
    override fun onDisable() {
        eventHandler?.onDisable()
        HandlerList.unregisterAll(this)
        GlobalEventManager.onDisable()
    }

    /**
     * As it says, function for plugin reload
     */
    fun reloadPlugin() {
        Files.configFile.reload()
        TranslationModule.reload()
    }

}


