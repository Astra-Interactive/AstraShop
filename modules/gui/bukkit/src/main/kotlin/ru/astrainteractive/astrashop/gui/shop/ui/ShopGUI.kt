package ru.astrainteractive.astrashop.gui.shop.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import org.bukkit.event.inventory.InventoryClickEvent
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.menu.inventory.PaginatedInventoryMenu
import ru.astrainteractive.astralibs.menu.inventory.model.InventorySize
import ru.astrainteractive.astralibs.menu.inventory.model.PageContext
import ru.astrainteractive.astralibs.menu.inventory.util.PageContextExt.getIndex
import ru.astrainteractive.astralibs.menu.inventory.util.PageContextExt.isFirstPage
import ru.astrainteractive.astralibs.menu.inventory.util.PageContextExt.isLastPage
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.addLore
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setIndex
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setItemStack
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setOnClickListener
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.core.PluginTranslation
import ru.astrainteractive.astrashop.domain.usecase.PriceCalculator
import ru.astrainteractive.astrashop.domain.util.ItemStackExt.toItemStack
import ru.astrainteractive.astrashop.gui.model.BukkitShopPlayerHolder
import ru.astrainteractive.astrashop.gui.renderer.ButtonsRenderer
import ru.astrainteractive.astrashop.gui.router.GuiRouter
import ru.astrainteractive.astrashop.gui.shop.presentation.ShopComponent
import ru.astrainteractive.astrashop.gui.shop.presentation.ShopComponent.Model
import ru.astrainteractive.astrashop.util.RoundExt.round

@Suppress("LongParameterList")
internal class ShopGUI(
    private val shopConfig: ShopConfig,
    override val playerHolder: BukkitShopPlayerHolder,
    private val translation: PluginTranslation,
    private val router: GuiRouter,
    private val shopComponent: ShopComponent,
    private val kyoriComponentSerializer: KyoriComponentSerializer
) : PaginatedInventoryMenu(), KyoriComponentSerializer by kyoriComponentSerializer {
    override val childComponents: List<CoroutineScope> = listOf(shopComponent)
    private val buttonsRenderer = ButtonsRenderer(
        translation = translation,
        menu = this,
        kyoriComponentSerializer = kyoriComponentSerializer
    )

    override val inventorySize: InventorySize = InventorySize.XL
    override var title: Component = shopConfig.options.title.let(::toComponent)

    override var pageContext: PageContext = PageContext(
        page = playerHolder.shopPage,
        maxItems = shopComponent.model.value.maxItemsAmount,
        maxItemsPerPage = inventorySize.size - InventorySize.XXS.size
    )

    override val nextPageButton: InventorySlot = buttonsRenderer.nextButton
    override val prevPageButton: InventorySlot = buttonsRenderer.prevButton
    private val backPageButton: InventorySlot = buttonsRenderer.backButton {
        val cleanPlayerHolder = BukkitShopPlayerHolder(playerHolder.player)
        val route = GuiRouter.Route.Shops(cleanPlayerHolder)
        menuScope.launch(Dispatchers.IO) { router.open(route) }
    }

    override fun onInventoryClicked(e: InventoryClickEvent) {
        super.onInventoryClicked(e)
        e.isCancelled = true
    }

    override fun onInventoryCreated() {
        shopComponent.model
            .onEach { pageContext = pageContext.copy(maxItems = shopComponent.model.value.maxItemsAmount) }
            .onEach { render() }
            .launchIn(menuScope)
    }

    private fun renderItemList(items: Map<String, ShopConfig.ShopItem>) {
        for (i in 0 until pageContext.maxItemsPerPage) {
            val index = pageContext.getIndex(i)
            val item = items[index.toString()] ?: continue
            val buyPrice = PriceCalculator.calculateBuyPrice(item, 1).round(2)
            val sellPrice = PriceCalculator.calculateSellPrice(item, 1).round(2)
            InventorySlot.Builder()
                .setIndex(i)
                .setItemStack(item.toItemStack())
                .addLore(translation.buttons.shopInfoStock(item.stock).let(::toComponent))
                .addLore(translation.buttons.shopInfoBuyPrice(buyPrice).let(::toComponent))
                .addLore(translation.buttons.shopInfoSellPrice(sellPrice).let(::toComponent))
                .addLore(translation.menu.menuDeleteItem.let(::toComponent))
                .apply {
                    if (item.isPurchaseInfinite) {
                        addLore(translation.shop.infinitePurchase.let(::toComponent))
                    }
                }
                .setOnClickListener {
                    val isValid = it.isLeftClick && !it.isShiftClick && shopComponent.model.value is Model.List
                    if (!isValid) return@setOnClickListener
                    val route = GuiRouter.Route.Buy(
                        playerHolder = playerHolder.copy(shopPage = pageContext.page),
                        shopConfig = shopConfig,
                        shopItem = item
                    )
                    menuScope.launch(Dispatchers.IO) { router.open(route) }
                }.build().setInventorySlot()
        }
    }

    override fun render() {
        super.render()
        backPageButton.setInventorySlot()
        if (!pageContext.isLastPage) nextPageButton.setInventorySlot()
        if (!pageContext.isFirstPage) prevPageButton.setInventorySlot()
        when (val state = shopComponent.model.value) {
            is Model.List -> {
                renderItemList(state.items)
            }

            Model.Loading -> Unit
        }
    }
}
