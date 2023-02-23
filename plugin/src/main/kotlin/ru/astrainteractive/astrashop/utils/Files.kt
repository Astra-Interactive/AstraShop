package ru.astrainteractive.astrashop.utils

import ru.astrainteractive.astralibs.file_manager.FileManager
import ru.astrainteractive.astrashop.AstraShop


/**
 * All plugin files such as config.yml and other should only be stored here!
 */
object Files {
    val configFile: FileManager = FileManager(AstraShop.instance,"config.yml")
}