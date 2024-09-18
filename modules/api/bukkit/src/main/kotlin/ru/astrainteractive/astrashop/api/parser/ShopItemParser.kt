package ru.astrainteractive.astrashop.api.parser

import ru.astrainteractive.astrashop.api.model.ShopConfig
import java.io.File

internal interface ShopItemParser {
    class ShopParseException(message: String) : Exception(message)

    /**
     * Save options of current [ShopConfig]
     */
    fun saveOptions(shopConfig: ShopConfig)

    /**
     * Save all items of current [ShopConfig]
     */
    fun saveItems(shopConfig: ShopConfig)

    /**
     * Parse shop file from [File]
     */
    fun parseShopFile(file: File): ShopConfig
}
