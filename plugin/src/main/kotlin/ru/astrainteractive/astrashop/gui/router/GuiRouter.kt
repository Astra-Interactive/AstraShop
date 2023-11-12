package ru.astrainteractive.astrashop.gui.router

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astrashop.gui.buy.BuyGUI
import ru.astrainteractive.astrashop.gui.quicksell.QuickSellGUI
import ru.astrainteractive.astrashop.gui.shop.ShopGUI
import ru.astrainteractive.astrashop.gui.shops.ShopsGUI

/**
 * @author antuEre
 */
class GuiRouter(
    private val scope: CoroutineScope,
    private val dispatchers: BukkitDispatchers
) : Router {

    private fun buildGUI(route: Router.Route) = when (route) {
        is Router.Route.Buy -> BuyGUI(
            shopConfig = route.shopConfig,
            item = route.item,
            playerHolder = route.playerHolder
        )
        is Router.Route.QuickSell -> QuickSellGUI(playerHolder = route.playerHolder)
        is Router.Route.Shop -> ShopGUI(shopConfig = route.shopConfig, playerHolder = route.playerHolder)
        is Router.Route.Shops -> ShopsGUI(playerHolder = route.playerHolder)
    }


    override fun open(route: Router.Route) {
        scope.launch(dispatchers.BukkitAsync) {
            val gui = buildGUI(route)
            withContext(dispatchers.BukkitMain) { gui.open() }
        }
    }
}
