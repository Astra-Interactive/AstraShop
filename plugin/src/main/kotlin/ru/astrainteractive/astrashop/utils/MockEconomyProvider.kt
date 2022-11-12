package ru.astrainteractive.astrashop.utils

import ru.astrainteractive.astralibs.utils.economy.IEconomyProvider
import java.util.*

object MockEconomyProvider : IEconomyProvider {
    override fun addMoney(uuid: UUID, amount: Double): Boolean = true

    override fun getBalance(uuid: UUID): Double? = 5000.0

    override fun takeMoney(uuid: UUID, amount: Double): Boolean = true

}