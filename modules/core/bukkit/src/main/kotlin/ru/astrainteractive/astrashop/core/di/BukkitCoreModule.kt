package ru.astrainteractive.astrashop.core.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.serialization.StringFormat
import ru.astrainteractive.astralibs.async.CoroutineFeature
import ru.astrainteractive.astralibs.async.DefaultBukkitDispatchers
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.menu.event.DefaultInventoryClickEvent
import ru.astrainteractive.astralibs.serialization.YamlStringFormat
import ru.astrainteractive.astrashop.core.LifecyclePlugin
import ru.astrainteractive.astrashop.core.PluginTranslation
import ru.astrainteractive.astrashop.core.di.factory.BukkitCurrencyEconomyProviderFactory
import ru.astrainteractive.astrashop.core.di.factory.ConfigKrateFactory
import ru.astrainteractive.astrashop.core.di.factory.CurrencyEconomyProviderFactory
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate

interface BukkitCoreModule : CoreModule {
    val plugin: LifecyclePlugin

    class Default(override val plugin: LifecyclePlugin) : BukkitCoreModule {

        private val inventoryClickEvent = DefaultInventoryClickEvent()

        override val currencyEconomyProviderFactory: CurrencyEconomyProviderFactory =
            BukkitCurrencyEconomyProviderFactory(plugin)

        override val yamlStringFormat: StringFormat = YamlStringFormat()

        override val translation = ConfigKrateFactory.create(
            fileNameWithoutExtension = "translations",
            stringFormat = yamlStringFormat,
            dataFolder = plugin.dataFolder,
            factory = ::PluginTranslation
        )

        override val dispatchers = DefaultBukkitDispatchers(plugin)

        override val scope: CoroutineScope = CoroutineFeature.Default(Dispatchers.IO)

        override val kyoriComponentSerializer = DefaultMutableKrate<KyoriComponentSerializer>(
            loader = { null },
            factory = { KyoriComponentSerializer.Legacy }
        )

        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onEnable = {
                inventoryClickEvent.onEnable(plugin)
            },
            onReload = {
                translation.loadAndGet()
                kyoriComponentSerializer.loadAndGet()
            },
            onDisable = {
                scope.cancel()
                inventoryClickEvent.onDisable()
            }
        )
    }
}
