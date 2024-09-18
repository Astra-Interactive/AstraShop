package ru.astrainteractive.astrashop.gui.router

import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.gui.model.ShopPlayerHolder
import java.util.UUID

interface GuiRouter {
    sealed interface Route {
        class Buy(
            val playerHolder: ShopPlayerHolder,
            val shopConfig: ShopConfig,
            val shopItem: ShopConfig.ShopItem,
        ) : Route

        class QuickSell(val playerUUID: UUID) : Route

        class Shop(
            val playerHolder: ShopPlayerHolder,
            val shopConfig: ShopConfig
        ) : Route

        class Shops(val playerHolder: ShopPlayerHolder) : Route
    }

    suspend fun open(route: Route)
}
