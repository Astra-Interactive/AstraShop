package ru.astrainteractive.astrashop.utils

import org.bukkit.configuration.ConfigurationSection
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.file_manager.FileManager
import ru.astrainteractive.astrashop.domain.models.ShopConfig
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

fun getFilesList() = AstraLibs.instance.dataFolder.listFiles().map {
    println(it.absolutePath)
    it
}

fun File.isYml() = extension.equals("yml", ignoreCase = true)

fun getYmlFiles() = getFilesList()?.filter { it.isYml() }?.map {
    FileManager(it.name)
}

val ShopConfig.fileManager: FileManager
    get() = FileManager(configName)