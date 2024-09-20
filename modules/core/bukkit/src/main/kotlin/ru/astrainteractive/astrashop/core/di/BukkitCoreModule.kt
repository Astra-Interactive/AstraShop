package ru.astrainteractive.astrashop.core.di

import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.DefaultBukkitDispatchers
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.menu.event.DefaultInventoryClickEvent
import ru.astrainteractive.astralibs.serialization.StringFormatExt.parse
import ru.astrainteractive.astralibs.serialization.StringFormatExt.writeIntoFile
import ru.astrainteractive.astralibs.serialization.YamlStringFormat
import ru.astrainteractive.astrashop.core.PluginTranslation
import ru.astrainteractive.astrashop.core.di.factory.BukkitCurrencyEconomyProviderFactory
import ru.astrainteractive.astrashop.core.di.factory.CurrencyEconomyProviderFactory
import ru.astrainteractive.klibs.kdi.Lateinit
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.Single

interface BukkitCoreModule : CoreModule {
    val plugin: Lateinit<JavaPlugin>
    val inventoryClickEvent: Single<EventListener>

    class Default : BukkitCoreModule {
        override val plugin: Lateinit<JavaPlugin> = Lateinit()
        override val inventoryClickEvent: Single<EventListener> = Single {
            DefaultInventoryClickEvent()
        }

        override val currencyEconomyProviderFactory: CurrencyEconomyProviderFactory by lazy {
            BukkitCurrencyEconomyProviderFactory(plugin.value)
        }

        override val translation = Reloadable {
            val serializer = YamlStringFormat()
            val config = plugin.value.dataFolder.resolve("translations.yml")
            serializer.parse<PluginTranslation>(config)
                .onFailure(Throwable::printStackTrace)
                .getOrElse { PluginTranslation() }
                .also { serializer.writeIntoFile(it, config) }
        }

        override val dispatchers by lazy {
            DefaultBukkitDispatchers(plugin.value)
        }

        override val scope: AsyncComponent by lazy {
            AsyncComponent.Default()
        }

        override val kyoriComponentSerializer: Reloadable<KyoriComponentSerializer> = Reloadable {
            KyoriComponentSerializer.Legacy
        }
    }
}
