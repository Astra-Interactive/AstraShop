package ru.astrainteractive.astrashop.core.di

import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.DefaultBukkitDispatchers
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.menu.event.DefaultInventoryClickEvent
import ru.astrainteractive.astralibs.serialization.StringFormatExt.parse
import ru.astrainteractive.astralibs.serialization.StringFormatExt.writeIntoFile
import ru.astrainteractive.astralibs.serialization.YamlStringFormat
import ru.astrainteractive.astrashop.core.LifecyclePlugin
import ru.astrainteractive.astrashop.core.PluginTranslation
import ru.astrainteractive.astrashop.core.di.factory.BukkitCurrencyEconomyProviderFactory
import ru.astrainteractive.astrashop.core.di.factory.CurrencyEconomyProviderFactory
import ru.astrainteractive.klibs.kdi.Reloadable

interface BukkitCoreModule : CoreModule {
    val plugin: LifecyclePlugin
    val inventoryClickEvent: EventListener

    class Default(override val plugin: LifecyclePlugin) : BukkitCoreModule {

        override val inventoryClickEvent = DefaultInventoryClickEvent()

        override val currencyEconomyProviderFactory: CurrencyEconomyProviderFactory =
            BukkitCurrencyEconomyProviderFactory(plugin)

        override val translation = Reloadable {
            val serializer = YamlStringFormat()
            val config = plugin.dataFolder.resolve("translations.yml")
            serializer.parse<PluginTranslation>(config)
                .onFailure(Throwable::printStackTrace)
                .getOrElse { PluginTranslation() }
                .also { serializer.writeIntoFile(it, config) }
        }

        override val dispatchers = DefaultBukkitDispatchers(plugin)

        override val scope: AsyncComponent = AsyncComponent.Default()

        override val kyoriComponentSerializer: Reloadable<KyoriComponentSerializer> = Reloadable {
            KyoriComponentSerializer.Legacy
        }

        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onEnable = {
                inventoryClickEvent.onEnable(plugin)
            },
            onReload = {
                translation.reload()
                kyoriComponentSerializer.reload()
            },
            onDisable = {
                scope.close()
                inventoryClickEvent.onDisable()
            }
        )
    }
}
