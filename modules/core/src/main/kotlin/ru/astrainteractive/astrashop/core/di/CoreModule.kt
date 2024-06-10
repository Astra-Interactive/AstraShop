package ru.astrainteractive.astrashop.core.di

import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.async.DefaultBukkitDispatchers
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.economy.EconomyProviderFactory
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.filemanager.DefaultFileConfigurationManager
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.menu.event.DefaultInventoryClickEvent
import ru.astrainteractive.astralibs.serialization.StringFormatExt.parse
import ru.astrainteractive.astralibs.serialization.StringFormatExt.writeIntoFile
import ru.astrainteractive.astralibs.serialization.YamlStringFormat
import ru.astrainteractive.astrashop.core.PluginTranslation
import ru.astrainteractive.klibs.kdi.Lateinit
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

interface CoreModule {
    val plugin: Lateinit<JavaPlugin>
    val translation: Reloadable<PluginTranslation>
    val economyProvider: Single<EconomyProvider>
    val dispatchers: Single<BukkitDispatchers>
    val scope: Single<AsyncComponent>
    val inventoryClickEvent: Single<EventListener>
    val kyoriComponentSerializer: Reloadable<KyoriComponentSerializer>

    class Default : CoreModule {
        override val plugin: Lateinit<JavaPlugin> = Lateinit()
        override val translation = Reloadable {
            val plugin by plugin
            val serializer = YamlStringFormat()
            val fileManager = DefaultFileConfigurationManager(plugin, "translations.yml")
            serializer.parse<PluginTranslation>(fileManager.configFile)
                .onFailure(Throwable::printStackTrace)
                .getOrElse { PluginTranslation() }
                .also { serializer.writeIntoFile(it, fileManager.configFile) }
        }
        override val economyProvider: Single<EconomyProvider> = Single {
            EconomyProviderFactory(plugin.value).create()
        }
        override val dispatchers: Single<BukkitDispatchers> = Single {
            val plugin by plugin
            DefaultBukkitDispatchers(plugin)
        }
        override val scope: Single<AsyncComponent> = Single {
            AsyncComponent.Default()
        }
        override val inventoryClickEvent: Single<EventListener> = Single {
            DefaultInventoryClickEvent()
        }
        override val kyoriComponentSerializer: Reloadable<KyoriComponentSerializer> = Reloadable {
            KyoriComponentSerializer.Legacy
        }
    }
}
