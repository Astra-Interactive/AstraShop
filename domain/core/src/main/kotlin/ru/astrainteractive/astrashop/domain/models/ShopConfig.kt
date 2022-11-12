package ru.astrainteractive.astrashop.domain.models

data class ShopConfig(
    val configName: String,
    val options: Options,
    val items: Map<String, ShopItem>
) {
    data class Options(
        val lore: List<String>,
        val permission: String,
        val workHours: String,
        val title: String,
        val titleItem: TitleItem
    )

    interface TitleItem

    interface ShopItem {
        val itemIndex: Int
        val median: Double
        var stock: Int
        val price: Double
        val priceMax: Double
        val priceMin: Double
    }
}