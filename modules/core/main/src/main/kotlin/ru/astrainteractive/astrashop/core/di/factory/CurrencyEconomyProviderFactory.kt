package ru.astrainteractive.astrashop.core.di.factory

import ru.astrainteractive.astralibs.economy.EconomyProvider

interface CurrencyEconomyProviderFactory {
    fun findByCurrencyId(currencyId: String): EconomyProvider?
    fun findDefault(): EconomyProvider?
}
