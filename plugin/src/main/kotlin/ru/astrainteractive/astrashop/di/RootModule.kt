package ru.astrainteractive.astrashop.di

import ru.astrainteractive.astralibs.Lateinit
import ru.astrainteractive.astralibs.Module
import ru.astrainteractive.astralibs.Reloadable
import ru.astrainteractive.astralibs.Single
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astrashop.AstraShop
import ru.astrainteractive.astrashop.domain.SpigotShopApi
import ru.astrainteractive.astrashop.util.PluginTranslation

interface RootModule : Module {
    val plugin: Lateinit<AstraShop>
    val translation: Reloadable<PluginTranslation>
    val spigotShopApi: Single<SpigotShopApi>
    val economyProvider: Single<EconomyProvider>
    val logger: Single<Logger>
    val dispatchers: Single<BukkitDispatchers>
    val scope: Single<AsyncComponent>
}
