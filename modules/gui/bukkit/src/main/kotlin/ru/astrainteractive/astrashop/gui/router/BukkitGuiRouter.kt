package ru.astrainteractive.astrashop.gui.router

import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import ru.astrainteractive.astralibs.menu.core.Menu
import ru.astrainteractive.astrashop.core.di.CoreModule
import ru.astrainteractive.astrashop.gui.buy.ui.BuyGUI
import ru.astrainteractive.astrashop.gui.model.BukkitShopPlayerHolder
import ru.astrainteractive.astrashop.gui.quicksell.ui.QuickSellGUI
import ru.astrainteractive.astrashop.gui.router.di.GuiModule
import ru.astrainteractive.astrashop.gui.shop.ui.ShopGUI
import ru.astrainteractive.astrashop.gui.shops.ui.ShopsGUI

internal class BukkitGuiRouter(
    private val coreModule: CoreModule,
    private val guiModule: GuiModule
) : GuiRouter {
    private fun quickSell(route: GuiRouter.Route.QuickSell) = QuickSellGUI(
        playerHolder = Bukkit.getPlayer(route.playerUUID)
            ?.let(::BukkitShopPlayerHolder)
            ?: error("Could not find player with UUID ${route.playerUUID}"),
        kyoriComponentSerializer = coreModule.kyoriComponentSerializer.cachedValue,
        translation = coreModule.translation.cachedValue,
        quickSellComponent = guiModule.createQuickSellComponent(route)
    )

    private fun shop(route: GuiRouter.Route.Shop) = ShopGUI(
        shopConfig = route.shopConfig,
        playerHolder = route.playerHolder as BukkitShopPlayerHolder,
        kyoriComponentSerializer = coreModule.kyoriComponentSerializer.cachedValue,
        translation = coreModule.translation.cachedValue,
        router = this,
        shopComponent = guiModule.createShopComponent(route)
    )

    private fun shops(route: GuiRouter.Route.Shops) = ShopsGUI(
        playerHolder = route.playerHolder as BukkitShopPlayerHolder,
        kyoriComponentSerializer = coreModule.kyoriComponentSerializer.cachedValue,
        translation = coreModule.translation.cachedValue,
        router = this,
        shopsComponent = guiModule.createShopsComponent(route)
    )

    private fun buy(route: GuiRouter.Route.Buy) = BuyGUI(
        item = route.shopItem,
        playerHolder = route.playerHolder as BukkitShopPlayerHolder,
        kyoriComponentSerializer = coreModule.kyoriComponentSerializer.cachedValue,
        translation = coreModule.translation.cachedValue,
        shopConfig = route.shopConfig,
        router = this,
        buyComponent = guiModule.createBuyComponent(route)
    )

    override suspend fun open(route: GuiRouter.Route) {
        val menu: Menu = withContext(coreModule.dispatchers.IO) {
            when (route) {
                is GuiRouter.Route.QuickSell -> quickSell(route)
                is GuiRouter.Route.Shops -> shops(route)
                is GuiRouter.Route.Shop -> shop(route)
                is GuiRouter.Route.Buy -> buy(route)
            }
        }
        withContext(coreModule.dispatchers.Main) {
            menu.open()
        }
    }
}
