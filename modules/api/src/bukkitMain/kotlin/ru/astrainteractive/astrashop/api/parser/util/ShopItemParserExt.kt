@file:Suppress("Filename")

package ru.astrainteractive.astrashop.api.parser.util

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.plugin.Plugin
import ru.astrainteractive.astralibs.filemanager.DefaultSpigotFileManager
import ru.astrainteractive.astralibs.filemanager.SpigotFileManager
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

internal fun getFilesList(plugin: Plugin) = plugin.dataFolder.listFiles()?.filterNotNull().orEmpty()

internal fun File.isYml() = extension.equals("yml", ignoreCase = true)

internal fun getYmlFiles(plugin: Plugin) = getFilesList(plugin).filter { it.isYml() }.map {
    DefaultSpigotFileManager(plugin, it.name)
}

internal fun ShopConfig.getFileManager(plugin: Plugin): SpigotFileManager = DefaultSpigotFileManager(plugin, configName)
