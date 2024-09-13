package ru.astrainteractive.astrashop.api.parser

import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import ru.astrainteractive.astralibs.logging.BukkitLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.api.model.SpigotShopItemStack
import ru.astrainteractive.astrashop.api.model.SpigotTitleItemStack
import ru.astrainteractive.astrashop.api.parser.ShopItemParser.ShopParseException
import ru.astrainteractive.astrashop.api.parser.util.associate
import ru.astrainteractive.astrashop.api.parser.util.getFile
import java.io.File

internal class ShopItemParserImpl(
    private val plugin: Plugin
) : ShopItemParser,
    Logger by BukkitLogger("ShopItemParser") {

    override fun saveOptions(shopConfig: ShopConfig) {
        val file = shopConfig.getFile(plugin)
        val fileConfiguration = YamlConfiguration.loadConfiguration(file)
        val optionsSection = fileConfiguration.getConfigurationSection("options")

        optionsSection?.set("lore", shopConfig.options.lore)
        optionsSection?.set("permission", shopConfig.options.permission)
        optionsSection?.set("workHours", shopConfig.options.workHours)
        optionsSection?.set("title", shopConfig.options.title)
        optionsSection?.set("index", shopConfig.options.index)
        optionsSection?.set("page", shopConfig.options.page)
        fileConfiguration.save(file)
    }

    override fun saveItems(shopConfig: ShopConfig) {
        val file = shopConfig.getFile(plugin)
        val fileConfiguration = YamlConfiguration.loadConfiguration(file)
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
            itemSection?.set("is_for_purchase", item.isForPurchase)
            itemSection?.set("is_for_sell", item.isForSell)
            itemSection?.set("sell_currency_id", item.sellCurrencyId)
            itemSection?.set("buy_currency_id", item.buyCurrencyId)
        }
        fileConfiguration.save(file)
    }

    override fun parseShopFile(file: File): ShopConfig {
        val fileConfiguration = YamlConfiguration.loadConfiguration(file)
        val optionsSections = fileConfiguration.getConfigurationSection("options")
            ?: throw ShopParseException("No options section in ${fileConfiguration.name} -> ${file.name}")
        val itemsSection = fileConfiguration.getConfigurationSection("items")

        val options = parseOption(optionsSections)
        val items = itemsSection?.associate { it.name to parseItem(it) } ?: emptyMap()
        return ShopConfig(
            configName = file.name,
            options = options,
            items = HashMap(items)
        )
    }

    private fun parseMaterialItem(s: ConfigurationSection): SpigotTitleItemStack.Default? {
        return SpigotTitleItemStack.Default(
            material = s.getString("material")?.let(Material::getMaterial) ?: return null,
            customModelData = s.getInt("customModelData"),
            name = s.getString("name").orEmpty(),
            lore = s.getStringList("lore").orEmpty()
        )
    }

    private fun parseItemsAdderItem(s: ConfigurationSection): SpigotTitleItemStack.ItemsAdder? {
        return SpigotTitleItemStack.ItemsAdder(
            namespaceId = s.getString("namespace_id") ?: return null,
            name = s.getString("name").orEmpty(),
            lore = s.getStringList("lore").orEmpty()
        )
    }

    private fun parseTitleItem(s: ConfigurationSection?): SpigotTitleItemStack {
        val createDefault = {
            SpigotTitleItemStack.Default(
                material = Material.PAPER,
                customModelData = 0,
                name = "ERROR",
            )
        }
        return s?.let(::parseMaterialItem) ?: s?.let(::parseItemsAdderItem) ?: createDefault.invoke()
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
            titleItem = s.getConfigurationSection("titleItem").let(::parseTitleItem),
            index = s.getInt("index"),
            page = s.getInt("page"),
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
                else -> {
                    error {
                        """Could not parse item type for ${s.name}
                           Material: ${s.getString("material")}
                           itemStack: ${s.getItemStack("itemStack")}
                           itemsAdder: ${s.getString("items_adder")}
                        """.trimIndent()
                    }
                    throw ShopParseException("Shop item should contain either itemStack or material")
                }
            },
            price = price,
            isForSell = s.getBoolean("is_for_sell", true),
            isForPurchase = s.getBoolean("is_for_purchase", true),
            isPurchaseInfinite = s.getBoolean("is_purchase_infinite", false),
            sellCurrencyId = s.getString("sell_currency_id"),
            buyCurrencyId = s.getString("buy_currency_id"),
        )
    }
}
