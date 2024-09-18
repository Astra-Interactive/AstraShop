package ru.astrainteractive.astrashop.core.di

import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astrashop.core.PluginTranslation
import ru.astrainteractive.astrashop.core.di.factory.CurrencyEconomyProviderFactory
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

interface CoreModule {
    val dispatchers: KotlinDispatchers
    val scope: AsyncComponent
    val translation: Reloadable<PluginTranslation>
    val kyoriComponentSerializer: Reloadable<KyoriComponentSerializer>
    val currencyEconomyProviderFactory: CurrencyEconomyProviderFactory
}
