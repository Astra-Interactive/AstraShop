package ru.astrainteractive.astrashop.core.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.StringFormat
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astrashop.core.PluginTranslation
import ru.astrainteractive.astrashop.core.di.factory.CurrencyEconomyProviderFactory
import ru.astrainteractive.klibs.kstorage.api.Krate
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

interface CoreModule {
    val lifecycle: Lifecycle

    val dispatchers: KotlinDispatchers
    val scope: CoroutineScope
    val translation: Krate<PluginTranslation>
    val kyoriComponentSerializer: Krate<KyoriComponentSerializer>
    val currencyEconomyProviderFactory: CurrencyEconomyProviderFactory
    val yamlStringFormat: StringFormat
}
