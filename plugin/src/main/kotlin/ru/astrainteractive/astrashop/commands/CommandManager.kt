import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.utils.registerCommand
import ru.astrainteractive.astrashop.commands.*
import ru.astrainteractive.astrashop.gui.shops.ShopsGUI
import ru.astrainteractive.astrashop.modules.TranslationModule


/**
 * Command handler for your plugin
 * It's better to create different executors for different commands
 * @see Reload
 */
class CommandManager {
    val translation by TranslationModule

    /**
     * Here you should declare commands for your plugin
     *
     * Commands stored in plugin.yml
     *
     * etemp has TabCompleter
     */
    init {
        reload()
        AstraLibs.registerCommand("shop") { sender, _ ->
            (sender as? Player)?.let {
                PluginScope.launch(Dispatchers.IO) {
                    ShopsGUI(it).open()
                }
            }

        }
    }


}