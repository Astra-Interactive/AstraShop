package ru.astrainteractive.astrashop.domain.models

data class ShopConfig<TITLE_ITEM, SHOP_ITEM>(
    val configName: String,
    val options: Options<TITLE_ITEM>,
    val items: HashMap<String, ShopItem<SHOP_ITEM>>
) {
    data class Options<TITLE_ITEM>(
        val lore: List<String>,
        val permission: String,
        val workHours: String,
        val title: String,
        val titleItem: TITLE_ITEM
    )

    data class ShopItem<SHOP_ITEM>(
        val itemIndex: Int,
        val median: Double,
        var stock: Int,
        var buyPrice: Double,
        var sellPrice: Double,
        val priceMax: Double,
        val priceMin: Double,
        val shopItem: SHOP_ITEM
    )
}
