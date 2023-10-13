package ru.astrainteractive.astrashop.di

import ru.astrainteractive.astrashop.api.interactors.BuyInteractor
import ru.astrainteractive.astrashop.api.interactors.SellInteractor
import ru.astrainteractive.klibs.kdi.Factory
import ru.astrainteractive.klibs.kdi.Module

interface InteractorsFactoryModule : Module {
    val buyInteractor: Factory<BuyInteractor>
    val sellInteractor: Factory<SellInteractor>
}
