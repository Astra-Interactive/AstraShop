package ru.astrainteractive.astrashop

import java.io.File
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.serialization.StringFormatExt.writeIntoFile
import ru.astrainteractive.astralibs.serialization.YamlStringFormat
import ru.astrainteractive.astrashop.api.calculator.PriceCalculator
import ru.astrainteractive.astrashop.di.RootModule

/**
 * Initial class for your plugin
 */
class AstraShop : JavaPlugin() {
    private val rootModule: RootModule by lazy {
        RootModule.Default()
    }

    init {
        rootModule.coreModule.plugin.initialize(this)
    }

    /**
     * This method called when server starts or PlugMan load plugin.
     */
    override fun onEnable() {
        val map = Material.entries.associate { it to PriceCalculator.getCraftsFor(it) }
        map.filter { it.value!=null }.let { map ->
            YamlStringFormat().writeIntoFile(map, File("./text.yaml").also { it.createNewFile() })
        }
        map.filter { it.value==null }.let { map ->
            YamlStringFormat().writeIntoFile(map, File("./text_null.yaml").also { it.createNewFile() })
        }
//        CommandManagerElement.Default(
//            rootModule.coreModule,
//            rootModule.routerModule
//        ).onEnable()
//
//        rootModule.coreModule.inventoryClickEvent.value.onEnable(this)
    }

    /**
     * This method called when server is shutting down or when PlugMan disable plugin.
     */
    override fun onDisable() {
//        HandlerList.unregisterAll(this)
//        rootModule.coreModule.inventoryClickEvent.value.onDisable()
//        rootModule.coreModule.scope.value.close()
//        Bukkit.getOnlinePlayers().forEach {
//            it.closeInventory()
//        }
    }

    /**
     * As it says, function for plugin reload
     */
    fun reloadPlugin() {
        rootModule.coreModule.translation.reload()
        Bukkit.getOnlinePlayers().forEach {
            it.closeInventory()
        }
    }
}
