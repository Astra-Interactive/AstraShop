package ru.astrainteractive.astrashop.util

import ru.astrainteractive.astralibs.economy.EconomyProvider
import java.util.*

object MockEconomyProvider : EconomyProvider {
    override fun addMoney(uuid: UUID, amount: Double): Boolean = true

    override fun getBalance(uuid: UUID): Double = 5000.0
    override fun hasAtLeast(uuid: UUID, amount: Double): Boolean = getBalance(uuid) >= amount

    override fun takeMoney(uuid: UUID, amount: Double): Boolean = true
}
