package ru.astrainteractive.astrashop.gui.router

import ru.astrainteractive.astrashop.api.util.SpigotShopConfigAlias
import ru.astrainteractive.astrashop.api.util.SpigotShopItemAlias
import ru.astrainteractive.astrashop.gui.util.ShopPlayerHolder

/**
 * @author antuEre
 */
interface Router {
    sealed interface Route {
        val playerHolder: ShopPlayerHolder

        data class Buy(
            val shopConfig: SpigotShopConfigAlias,
            val item: SpigotShopItemAlias,
            override val playerHolder: ShopPlayerHolder
        ) : Route

        data class QuickSell(override val playerHolder: ShopPlayerHolder) : Route
        data class Shop(val shopConfig: SpigotShopConfigAlias, override val playerHolder: ShopPlayerHolder) : Route
        data class Shops(override val playerHolder: ShopPlayerHolder) : Route
    }

    fun open(route: Route)
}
