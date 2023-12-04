package ru.astrainteractive.astrashop.api.parser

import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.plugin.Plugin
import ru.astrainteractive.astralibs.filemanager.SpigotFileManager
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.api.model.SpigotShopItemStack
import ru.astrainteractive.astrashop.api.model.SpigotTitleItemStack
import ru.astrainteractive.astrashop.api.parser.ShopItemParser.ShopParseException
import ru.astrainteractive.astrashop.api.util.associate
import ru.astrainteractive.astrashop.api.util.getFileManager
import kotlin.math.max

internal class ShopItemParserImpl(private val plugin: Plugin) : ShopItemParser {

    override fun saveOptions(shopConfig: ShopConfig) {
        val fileManager = shopConfig.getFileManager(plugin)
        val fileConfiguration = fileManager.fileConfiguration
        val optionsSection = fileConfiguration.getConfigurationSection("options")

        optionsSection?.set("lore", shopConfig.options.lore)
        optionsSection?.set("permission", shopConfig.options.permission)
        optionsSection?.set("workHours", shopConfig.options.workHours)
        optionsSection?.set("title", shopConfig.options.title)
        fileManager.save()
    }

    override fun saveItems(shopConfig: ShopConfig) {
        val fileManager = shopConfig.getFileManager(plugin)
        val fileConfiguration = fileManager.fileConfiguration
        fileConfiguration.set("items", null)
        shopConfig.items.forEach { index, item ->
            val path = "items.$index"
            if (!fileConfiguration.contains(path)) {
                fileConfiguration.createSection(path)
            }

            val itemSection = fileConfiguration.getConfigurationSection(path)

            when (val item = item.shopItem) {
                is SpigotShopItemStack.ItemStackStack -> itemSection?.set("itemStack", item.itemStack)
                is SpigotShopItemStack.Material -> itemSection?.set("material", item.material.name)
            }
            itemSection?.set("median", item.median)
            itemSection?.set("stock", item.stock)
            itemSection?.set("buyPrice", item.buyPrice)
            itemSection?.set("sellPrice", item.sellPrice)
            itemSection?.set("priceMax", item.priceMax)
            itemSection?.set("priceMin", item.priceMin)
        }
//        fileConfiguration.save(fileManager.configFile)
        fileManager.save()
    }

    override fun parseShopFile(fileManager: SpigotFileManager): ShopConfig {
        val fileConfiguration = fileManager.fileConfiguration
        val optionsSections = fileConfiguration.getConfigurationSection("options")
            ?: throw ShopParseException("No options section in ${fileConfiguration.name}")
        val itemsSection = fileConfiguration.getConfigurationSection("items")

        val options = parseOption(optionsSections)
        val items = itemsSection?.associate { it.name to parseItem(it) } ?: emptyMap()
        return ShopConfig(
            configName = fileManager.name,
            options = options,
            items = HashMap(items)
        )
    }

    private fun parseTitleItem(s: ConfigurationSection?): SpigotTitleItemStack {
        return SpigotTitleItemStack(
            material = s?.getString("material")?.let(Material::getMaterial) ?: Material.EMERALD,
            customModelData = s?.getInt("customModelData") ?: 0,
            name = s?.getString("name") ?: "",
            lore = s?.getStringList("lore") ?: emptyList()
        )
    }

    /**
     * Parse here section of options
     */
    private fun parseOption(s: ConfigurationSection): ShopConfig.Options {
        return ShopConfig.Options(
            lore = s.getStringList("lore") ?: emptyList(),
            permission = s.getString("permission") ?: "",
            workHours = s.getString("workHours") ?: "",
            title = StringDesc.Raw(s.getString("title") ?: ""),
            titleItem = s.getConfigurationSection("titleItem").let(::parseTitleItem)
        )
    }

    /**
     * Parse here section of items.<item>
     */
    private fun parseItem(s: ConfigurationSection): ShopConfig.ShopItem {
        val itemStack = s.getItemStack("itemStack")
        val material = s.getString("material")?.let(Material::getMaterial)

        val itemIndex = s.name.toIntOrNull() ?: throw ShopParseException("Item in items.<item> should be number!")
        val median = s.getDouble("median", 0.0)
        val stock = s.getInt("stock", -1)
        val buyPrice = s.getDouble("buyPrice", 0.0)
        val sellPrice = s.getDouble("sellPrice", 0.0)
        val buySellMaxPrice = max(buyPrice, sellPrice)
        val minPrice = s.getDouble("priceMin", 0.0).coerceAtMost(buySellMaxPrice)
        val maxPrice = s.getDouble("priceMax", Double.MAX_VALUE).coerceAtLeast(buySellMaxPrice)
        if (itemStack != null) {
            return ShopConfig.ShopItem(
                itemIndex = itemIndex,
                median = median,
                stock = stock,
                buyPrice = buyPrice,
                sellPrice = sellPrice,
                priceMax = maxPrice,
                priceMin = minPrice,
                shopItem = SpigotShopItemStack.ItemStackStack(itemStack)
            )
        } else if (material != null) {
            return ShopConfig.ShopItem(
                itemIndex = itemIndex,
                median = median,
                stock = stock,
                buyPrice = buyPrice,
                sellPrice = sellPrice,
                priceMax = maxPrice,
                priceMin = minPrice,
                shopItem = SpigotShopItemStack.Material(material)
            )
        } else {
            throw ShopParseException("Shop item should contain either itemStack or material")
        }
    }
}
