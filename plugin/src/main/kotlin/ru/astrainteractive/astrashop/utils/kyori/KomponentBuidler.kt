package ru.astrainteractive.astrashop.utils.kyori

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent

class KomponentBuidler {
    private var texts = mutableListOf<TextComponent>()

    fun text(text: String, block: TextKomponentBuilder.() -> Unit = {}) {
        TextKomponentBuilder(text).apply(block).build().also(texts::add)
    }

    fun build(): TextComponent {
        val parent = texts.firstOrNull() ?: Component.empty()
        texts.removeFirstOrNull()
        texts.forEach(parent::append)
        return parent
    }
}