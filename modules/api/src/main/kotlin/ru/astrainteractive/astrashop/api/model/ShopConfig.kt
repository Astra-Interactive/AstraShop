package ru.astrainteractive.astrashop.api.model

import ru.astrainteractive.astralibs.string.StringDesc

data class ShopConfig(
    val configName: String,
    val options: Options,
    val items: HashMap<String, ShopItem>
) {
    data class Options(
        val lore: List<String>,
        val permission: String,
        val workHours: String,
        val title: StringDesc.Raw,
        val titleItem: TitleItemStack
    )

    data class ShopItem(
        val itemIndex: Int,
        val median: Double,
        var stock: Int,
        var buyPrice: Double,
        var sellPrice: Double,
        val priceMax: Double,
        val priceMin: Double,
        val shopItem: ShopItemStack
    )
}
