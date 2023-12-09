package ru.astrainteractive.astrashop.gui.router

import org.bukkit.entity.Player
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.gui.model.ShopPlayerHolder

interface GuiRouter {
    sealed interface Route {
        class Buy(
            val playerHolder: ShopPlayerHolder,
            val shopConfig: ShopConfig,
            val shopItem: ShopConfig.ShopItem,
        ) : Route

        class QuickSell(val player: Player) : Route

        class Shop(
            val playerHolder: ShopPlayerHolder,
            val shopConfig: ShopConfig
        ) : Route

        class Shops(val playerHolder: ShopPlayerHolder) : Route
    }

    suspend fun open(route: Route)
}
