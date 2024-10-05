package ru.astrainteractive.astrashop.api.parser.editor

import org.bukkit.configuration.ConfigurationSection

interface SectionEditor<T> {
    fun read(s: ConfigurationSection): T
    fun write(value: T, s: ConfigurationSection)
}
