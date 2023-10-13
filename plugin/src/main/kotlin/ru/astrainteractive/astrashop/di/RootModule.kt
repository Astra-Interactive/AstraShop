package ru.astrainteractive.astrashop.di

import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astrashop.AstraShop
import ru.astrainteractive.astrashop.api.impl.SpigotShopApi
import ru.astrainteractive.astrashop.domain.di.DomainModule
import ru.astrainteractive.astrashop.util.PluginTranslation
import ru.astrainteractive.klibs.kdi.Lateinit
import ru.astrainteractive.klibs.kdi.Module
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.Single

interface RootModule : Module {
    val plugin: Lateinit<AstraShop>
    val translation: Reloadable<PluginTranslation>
    val spigotShopApi: Single<SpigotShopApi>
    val economyProvider: Single<EconomyProvider>
    val logger: Single<Logger>
    val dispatchers: Single<BukkitDispatchers>
    val scope: Single<AsyncComponent>

    val domainModule: DomainModule
}
