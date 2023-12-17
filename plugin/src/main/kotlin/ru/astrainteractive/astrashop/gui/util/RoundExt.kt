package ru.astrainteractive.astrashop.gui.util

object RoundExt {
    fun Int.round(decimals: Int = 2) = (this as Number).round(decimals)
    fun Double.round(decimals: Int = 2) = (this as Number).round(decimals)
    fun Number.round(decimals: Int = 2): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return kotlin.math.round(this.toDouble() * multiplier) / multiplier
    }
}
