@file:Suppress("Filename")

package ru.astrainteractive.astrashop.domain.utils

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.plugin.Plugin
import ru.astrainteractive.astralibs.filemanager.DefaultSpigotFileManager
import ru.astrainteractive.astralibs.filemanager.SpigotFileManager
import ru.astrainteractive.astrashop.domain.model.ShopConfig
import java.io.File

public inline fun ConfigurationSection.forEach(
    deep: Boolean = false,
    action: (ConfigurationSection) -> Unit
) {
    getKeys(deep).forEach {
        getConfigurationSection(it)?.let(action)
    }
}

public inline fun <T> ConfigurationSection.map(
    deep: Boolean = false,
    action: (ConfigurationSection) -> T
): List<T> {
    return getKeys(deep).mapNotNull {
        getConfigurationSection(it)?.let(action)
    }
}

public inline fun <T, K> ConfigurationSection.associate(
    deep: Boolean = false,
    action: (ConfigurationSection) -> Pair<T, K>
): Map<T, K> {
    return getKeys(deep).mapNotNull {
        getConfigurationSection(it)?.let(action)
    }.associate { it }
}

fun getFilesList(plugin: Plugin) = plugin.dataFolder.listFiles().map {
    it
}

fun File.isYml() = extension.equals("yml", ignoreCase = true)

fun getYmlFiles(plugin: Plugin) = getFilesList(plugin)?.filter { it.isYml() }?.map {
    DefaultSpigotFileManager(plugin, it.name)
}

fun ShopConfig.getFileManager(plugin: Plugin): SpigotFileManager = DefaultSpigotFileManager(plugin, configName)
