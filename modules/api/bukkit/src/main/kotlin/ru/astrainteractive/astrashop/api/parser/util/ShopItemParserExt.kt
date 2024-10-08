@file:Suppress("Filename")

package ru.astrainteractive.astrashop.api.parser.util

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.plugin.Plugin
import ru.astrainteractive.astrashop.api.model.ShopConfig
import java.io.File

internal inline fun <T, K> ConfigurationSection.associate(
    deep: Boolean = false,
    action: (ConfigurationSection) -> Pair<T, K>
): Map<T, K> {
    return getKeys(deep).mapNotNull {
        getConfigurationSection(it)?.let(action)
    }.associate { it }
}

internal fun ShopConfig.getFile(plugin: Plugin): File = plugin.dataFolder
    .resolve("shops")
    .resolve(configName)

internal fun getYmlFiles(plugin: Plugin) = plugin.dataFolder
    .resolve("shops")
    .listFiles()
    .orEmpty()
    .filterNotNull()
    .filter { it.extension.equals("yml", ignoreCase = true) }
