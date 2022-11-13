package ru.astrainteractive.astrashop.utils.kyori

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.TextColor

class TextKomponentBuilder(val text: String) {
    private var color: Int = 0xFFFFFF
    fun color(color: Int) {
        this.color = color
    }

    fun build(): TextComponent {
        return Component.text(text).color(TextColor.color(color))
    }

}
fun component(block: KomponentBuidler.() -> Unit): TextComponent {
    return KomponentBuidler().apply(block).build()
}