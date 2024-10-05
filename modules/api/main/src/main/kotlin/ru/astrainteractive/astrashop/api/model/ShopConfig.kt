package ru.astrainteractive.astrashop.api.model

import ru.astrainteractive.astralibs.string.StringDesc

data class ShopConfig(
    val configName: String,
    val options: Options,
    val items: HashMap<String, ShopItem>
) {
    data class Options(
        val page: Int,
        val index: Int,
        val title: StringDesc.Raw,
        val titleItem: TitleItemStack
    )

    data class ShopItem(
        val itemIndex: Int,
        val sellCurrencyId: String? = null,
        val buyCurrencyId: String? = null,
        val isForSell: Boolean,
        val isPurchaseInfinite: Boolean,
        val isForPurchase: Boolean,
        var stock: Int,
        val price: Double,
        val shopItem: ShopItemStack
    )
}
