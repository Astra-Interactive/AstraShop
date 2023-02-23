package ru.astrainteractive.astrashop

import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.menu.SharedInventoryClickEvent
import ru.astrainteractive.astralibs.utils.KotlinPlugin
import ru.astrainteractive.astralibs.utils.setupWithSpigot
import ru.astrainteractive.astrashop.commands.CommandManager
import ru.astrainteractive.astrashop.modules.TranslationModule
import ru.astrainteractive.astrashop.utils.Files

/**
 * Initial class for your plugin
 */
class AstraShop : JavaPlugin() {
    companion object: KotlinPlugin<AstraShop>()

    init {
        instance = this
    }

    /**
     * This method called when server starts or PlugMan load plugin.
     */
    override fun onEnable() {
        Logger.setupWithSpigot(this,"AstraShop")
        CommandManager.enable()
        SharedInventoryClickEvent.onEnable(this)
    }

    /**
     * This method called when server is shutting down or when PlugMan disable plugin.
     */
    override fun onDisable() {
        HandlerList.unregisterAll(this)
        AstraShop.onDisable()
        AstraShop.close()
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


