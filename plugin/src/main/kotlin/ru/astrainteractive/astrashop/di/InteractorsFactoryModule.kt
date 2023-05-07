package ru.astrainteractive.astrashop.di

import ru.astrainteractive.astralibs.Factory
import ru.astrainteractive.astralibs.Module
import ru.astrainteractive.astrashop.domain.interactors.BuyInteractor
import ru.astrainteractive.astrashop.domain.interactors.SellInteractor

interface InteractorsFactoryModule : Module {
    val buyInteractor: Factory<BuyInteractor>
    val sellInteractor: Factory<SellInteractor>
}
