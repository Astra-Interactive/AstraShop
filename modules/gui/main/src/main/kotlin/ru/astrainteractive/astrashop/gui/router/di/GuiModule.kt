package ru.astrainteractive.astrashop.gui.router.di

import ru.astrainteractive.astrashop.api.di.ApiModule
import ru.astrainteractive.astrashop.core.di.CoreModule
import ru.astrainteractive.astrashop.domain.di.DomainModule
import ru.astrainteractive.astrashop.gui.buy.presentation.BuyComponent
import ru.astrainteractive.astrashop.gui.buy.presentation.DefaultBuyComponent
import ru.astrainteractive.astrashop.gui.quicksell.presentation.DefaultQuickSellComponent
import ru.astrainteractive.astrashop.gui.quicksell.presentation.QuickSellComponent
import ru.astrainteractive.astrashop.gui.router.GuiRouter
import ru.astrainteractive.astrashop.gui.shop.presentation.DefaultShopComponent
import ru.astrainteractive.astrashop.gui.shop.presentation.ShopComponent
import ru.astrainteractive.astrashop.gui.shops.presentation.DefaultShopsComponent
import ru.astrainteractive.astrashop.gui.shops.presentation.ShopsComponent

interface GuiModule {
    fun createQuickSellComponent(route: GuiRouter.Route.QuickSell): QuickSellComponent
    fun createShopComponent(route: GuiRouter.Route.Shop): ShopComponent
    fun createShopsComponent(route: GuiRouter.Route.Shops): ShopsComponent
    fun createBuyComponent(route: GuiRouter.Route.Buy): BuyComponent

    class Default(
        private val coreModule: CoreModule,
        private val apiModule: ApiModule,
        private val domainModule: DomainModule
    ) : GuiModule {
        override fun createQuickSellComponent(route: GuiRouter.Route.QuickSell): QuickSellComponent {
            return DefaultQuickSellComponent(
                translation = coreModule.translation.value,
                shopApi = apiModule.shopApi,
                sellInteractor = domainModule.sellInteractor,
            )
        }

        override fun createShopComponent(route: GuiRouter.Route.Shop): ShopComponent {
            return DefaultShopComponent(
                shopApi = apiModule.shopApi,
                shopFileName = route.shopConfig.configName,
            )
        }

        override fun createShopsComponent(route: GuiRouter.Route.Shops): ShopsComponent {
            return DefaultShopsComponent(
                api = apiModule.shopApi,
            )
        }

        override fun createBuyComponent(route: GuiRouter.Route.Buy): BuyComponent {
            return DefaultBuyComponent(
                shopFileName = route.shopConfig.configName,
                shopApi = apiModule.shopApi,
                sellInteractor = domainModule.sellInteractor,
                currencyEconomyProviderFactory = coreModule.currencyEconomyProviderFactory,
                buyInteractor = domainModule.buyInteractor,
                playerUUID = route.playerHolder.playerUUID,
                shopItem = route.shopItem
            )
        }
    }
}
