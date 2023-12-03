package ru.astrainteractive.astrashop.gui.router

import kotlinx.coroutines.withContext
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.menu.menu.Menu
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astrashop.api.di.ApiModule
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.core.di.CoreModule
import ru.astrainteractive.astrashop.domain.di.DomainModule
import ru.astrainteractive.astrashop.gui.buy.presentation.DefaultBuyComponent
import ru.astrainteractive.astrashop.gui.buy.ui.BuyGUI
import ru.astrainteractive.astrashop.gui.model.ShopPlayerHolder
import ru.astrainteractive.astrashop.gui.quicksell.presentation.QuickSellController
import ru.astrainteractive.astrashop.gui.quicksell.ui.QuickSellGUI
import ru.astrainteractive.astrashop.gui.shop.presentation.DefaultShopComponent
import ru.astrainteractive.astrashop.gui.shop.ui.ShopGUI
import ru.astrainteractive.astrashop.gui.shops.presentation.DefaultShopsComponent
import ru.astrainteractive.astrashop.gui.shops.ui.ShopsGUI

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

class GuiRouterImpl(
    private val coreModule: CoreModule,
    private val apiModule: ApiModule,
    private val domainModule: DomainModule
) : GuiRouter {
    private fun quickSell(route: GuiRouter.Route.QuickSell) = QuickSellGUI(
        playerHolder = ShopPlayerHolder(route.player),
        translationContext = coreModule.translationContext,
        translation = coreModule.translation.value,
        controller = QuickSellController(
            translation = coreModule.translation.value,
            shopApi = apiModule.shopApi,
            sellInteractor = domainModule.sellInteractor,
        )
    )

    private fun shop(route: GuiRouter.Route.Shop) = ShopGUI(
        shopTitle = route.shopConfig.configName.let(StringDesc::Raw),
        playerHolder = route.playerHolder,
        translationContext = coreModule.translationContext,
        translation = coreModule.translation.value,
        calculatePriceUseCase = domainModule.calculatePriceUseCase,
        shopComponentFactory = {
            DefaultShopComponent(
                dataSource = apiModule.shopApi,
                shopFileName = route.shopConfig.configName,
                pagingProvider = it,
                router = this
            )
        }
    )

    private fun shops(route: GuiRouter.Route.Shops) = ShopsGUI(
        playerHolder = route.playerHolder,
        translationContext = coreModule.translationContext,
        translation = coreModule.translation.value,
        router = this,
        shopsComponent = DefaultShopsComponent(
            api = apiModule.shopApi,
        )
    )

    private fun buy(route: GuiRouter.Route.Buy) = BuyGUI(
        item = route.shopItem,
        playerHolder = route.playerHolder,
        translationContext = coreModule.translationContext,
        translation = coreModule.translation.value,
        calculatePriceUseCase = domainModule.calculatePriceUseCase,
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
