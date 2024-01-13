package ru.astrainteractive.astrashop.gui.router

import kotlinx.coroutines.withContext
import ru.astrainteractive.astralibs.menu.menu.Menu
import ru.astrainteractive.astrashop.api.di.ApiModule
import ru.astrainteractive.astrashop.core.di.CoreModule
import ru.astrainteractive.astrashop.domain.di.DomainModule
import ru.astrainteractive.astrashop.gui.buy.presentation.DefaultBuyComponent
import ru.astrainteractive.astrashop.gui.buy.ui.BuyGUI
import ru.astrainteractive.astrashop.gui.model.ShopPlayerHolder
import ru.astrainteractive.astrashop.gui.quicksell.presentation.DefaultQuickSellComponent
import ru.astrainteractive.astrashop.gui.quicksell.ui.QuickSellGUI
import ru.astrainteractive.astrashop.gui.shop.presentation.DefaultShopComponent
import ru.astrainteractive.astrashop.gui.shop.ui.ShopGUI
import ru.astrainteractive.astrashop.gui.shops.presentation.DefaultShopsComponent
import ru.astrainteractive.astrashop.gui.shops.ui.ShopsGUI

internal class GuiRouterImpl(
    private val coreModule: CoreModule,
    private val apiModule: ApiModule,
    private val domainModule: DomainModule
) : GuiRouter {
    private fun quickSell(route: GuiRouter.Route.QuickSell) = QuickSellGUI(
        playerHolder = ShopPlayerHolder(route.player),
        kyoriComponentSerializer = coreModule.kyoriComponentSerializer.value,
        translation = coreModule.translation.value,
        quickSellComponent = DefaultQuickSellComponent(
            translation = coreModule.translation.value,
            shopApi = apiModule.shopApi,
            sellInteractor = domainModule.sellInteractor,
        )
    )

    private fun shop(route: GuiRouter.Route.Shop) = ShopGUI(
        shopConfig = route.shopConfig,
        playerHolder = route.playerHolder,
        kyoriComponentSerializer = coreModule.kyoriComponentSerializer.value,
        translation = coreModule.translation.value,
        router = this,
        shopComponent = DefaultShopComponent(
            shopApi = apiModule.shopApi,
            shopFileName = route.shopConfig.configName,
        )
    )

    private fun shops(route: GuiRouter.Route.Shops) = ShopsGUI(
        playerHolder = route.playerHolder,
        kyoriComponentSerializer = coreModule.kyoriComponentSerializer.value,
        translation = coreModule.translation.value,
        router = this,
        shopsComponent = DefaultShopsComponent(
            api = apiModule.shopApi,
        )
    )

    private fun buy(route: GuiRouter.Route.Buy) = BuyGUI(
        item = route.shopItem,
        playerHolder = route.playerHolder,
        kyoriComponentSerializer = coreModule.kyoriComponentSerializer.value,
        translation = coreModule.translation.value,
        shopConfig = route.shopConfig,
        router = this,
        buyComponent = DefaultBuyComponent(
            shopFileName = route.shopConfig.configName,
            shopApi = apiModule.shopApi,
            sellInteractor = domainModule.sellInteractor,
            economy = coreModule.economyProvider.value,
            buyInteractor = domainModule.buyInteractor,
            playerUUID = route.playerHolder.player.uniqueId,
            shopItem = route.shopItem
        )
    )

    override suspend fun open(route: GuiRouter.Route) {
        val menu: Menu = withContext(coreModule.dispatchers.value.BukkitAsync) {
            when (route) {
                is GuiRouter.Route.QuickSell -> quickSell(route)
                is GuiRouter.Route.Shops -> shops(route)
                is GuiRouter.Route.Shop -> shop(route)
                is GuiRouter.Route.Buy -> buy(route)
            }
        }
        withContext(coreModule.dispatchers.value.BukkitMain) {
            menu.open()
        }
    }
}
