package ru.astrainteractive.astrashop.domain.models

data class ShopConfig(
    val configName: String,
    val options: Options,
    val items: HashMap<String, ShopItem>
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
        fun getSellPrice() = Math.round(price * 0.4 * 10.0) / 10.0
    }
}