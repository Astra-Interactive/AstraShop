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
import ru.astrainteractive.astrashop.api.parser.util.associate
import ru.astrainteractive.astrashop.api.parser.util.getFileManager

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
        shopConfig.items.forEach { (index, item) ->
            val path = "items.$index"
            if (!fileConfiguration.contains(path)) {
                fileConfiguration.createSection(path)
            }

            val itemSection = fileConfiguration.getConfigurationSection(path)

            when (val shopItemStack = item.shopItem) {
                is SpigotShopItemStack.ItemStackStack -> itemSection?.set("itemStack", shopItemStack.itemStack)
                is SpigotShopItemStack.Material -> itemSection?.set("material", shopItemStack.material.name)
                is SpigotShopItemStack.ItemsAdder -> itemSection?.set("items_adder", shopItemStack.namespaceId)
            }
            itemSection?.set("stock", item.stock)
            itemSection?.set("price", item.price)
            itemSection?.set("is_purchase_infinite", item.isPurchaseInfinite)
        }
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
            lore = s.getStringList("lore").orEmpty(),
            permission = s.getString("permission").orEmpty(),
            workHours = s.getString("workHours").orEmpty(),
            title = StringDesc.Raw(s.getString("title").orEmpty()),
            titleItem = s.getConfigurationSection("titleItem").let(::parseTitleItem)
        )
    }

    /**
     * Parse here section of items.<item>
     */
    private fun parseItem(s: ConfigurationSection): ShopConfig.ShopItem {
        val itemStack = s.getItemStack("itemStack")
        val material = s.getString("material")?.let(Material::getMaterial)
        val itemsAdder = s.getString("items_adder")

        val itemIndex = s.name.toIntOrNull() ?: throw ShopParseException("Item in items.<item> should be number!")
        val stock = s.getInt("stock", -1)
        val price = s.getDouble("price", 0.0)
        return ShopConfig.ShopItem(
            itemIndex = itemIndex,
            stock = stock,
            shopItem = when {
                itemStack != null -> SpigotShopItemStack.ItemStackStack(itemStack)
                material != null -> SpigotShopItemStack.Material(material)
                itemsAdder != null -> SpigotShopItemStack.ItemsAdder(itemsAdder)
                else -> throw ShopParseException("Shop item should contain either itemStack or material")
            },
            price = price,
            isForSell = s.getBoolean("is_for_sell", true),
            isForPurchase = s.getBoolean("is_for_purchase", true),
            isPurchaseInfinite = s.getBoolean("is_purchase_infinite", false)
        )
    }
}
