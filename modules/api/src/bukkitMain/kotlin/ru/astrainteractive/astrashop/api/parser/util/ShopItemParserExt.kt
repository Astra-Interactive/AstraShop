@file:Suppress("Filename")

package ru.astrainteractive.astrashop.api.parser.util

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.plugin.Plugin
import ru.astrainteractive.astralibs.filemanager.DefaultFileConfigurationManager
import ru.astrainteractive.astralibs.filemanager.FileConfigurationManager
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

internal fun ShopConfig.getFileManager(plugin: Plugin): FileConfigurationManager = DefaultFileConfigurationManager(
    plugin,
    configName
)

internal fun getYmlFiles(plugin: Plugin) = File(plugin.dataFolder, "shops")
    .listFiles()
    .orEmpty()
    .filterNotNull()
    .filter { it.extension.equals("yml", ignoreCase = true) }
    .map { DefaultFileConfigurationManager(plugin, "shops${File.separator}${it.name}") }
