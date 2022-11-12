package ru.astrainteractive.astrashop.modules

import ru.astrainteractive.astralibs.di.module
import ru.astrainteractive.astralibs.di.reloadable
import ru.astrainteractive.astralibs.utils.economy.IEconomyProvider
import ru.astrainteractive.astralibs.utils.economy.VaultEconomyProvider
import ru.astrainteractive.astrashop.domain.SpigotDataSource
import ru.astrainteractive.astrashop.utils.MockEconomyProvider
import ru.astrainteractive.astrashop.utils.PluginTranslation
import java.util.*


val DataSourceModule = module {
    SpigotDataSource()
}
val TranslationModule = reloadable {
    PluginTranslation()
}
val EconomyModule = reloadable<IEconomyProvider> {
    MockEconomyProvider
//    VaultEconomyProvider.also { VaultEconomyProvider.onEnable() }
}