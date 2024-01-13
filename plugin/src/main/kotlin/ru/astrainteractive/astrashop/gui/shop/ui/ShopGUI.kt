package ru.astrainteractive.astrashop.gui.shop.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import org.bukkit.event.inventory.InventoryClickEvent
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.menu.menu.MenuSize
import ru.astrainteractive.astralibs.menu.menu.PaginatedMenu
import ru.astrainteractive.astralibs.menu.menu.addLore
import ru.astrainteractive.astralibs.menu.menu.setIndex
import ru.astrainteractive.astralibs.menu.menu.setItemStack
import ru.astrainteractive.astralibs.menu.menu.setOnClickListener
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.core.PluginTranslation
import ru.astrainteractive.astrashop.domain.usecase.PriceCalculator
import ru.astrainteractive.astrashop.domain.util.ItemStackExt.toItemStack
import ru.astrainteractive.astrashop.gui.model.ShopPlayerHolder
import ru.astrainteractive.astrashop.gui.renderer.ButtonsRenderer
import ru.astrainteractive.astrashop.gui.router.GuiRouter
import ru.astrainteractive.astrashop.gui.shop.presentation.ShopComponent
import ru.astrainteractive.astrashop.gui.shop.presentation.ShopComponent.Model
import ru.astrainteractive.astrashop.util.RoundExt.round

@Suppress("LongParameterList")
class ShopGUI(
    private val shopConfig: ShopConfig,
    override val playerHolder: ShopPlayerHolder,
    private val translation: PluginTranslation,
    private val router: GuiRouter,
    private val shopComponent: ShopComponent,
    private val kyoriComponentSerializer: KyoriComponentSerializer
) : PaginatedMenu(), KyoriComponentSerializer by kyoriComponentSerializer {
    override val childComponents: List<CoroutineScope> = listOf(shopComponent)
    private val buttonsRenderer = ButtonsRenderer(
        translation = translation,
        menu = this,
        kyoriComponentSerializer = kyoriComponentSerializer
    )

    override val menuSize: MenuSize = MenuSize.XL
    override var menuTitle: Component = shopConfig.options.title.let(::toComponent)
    override var page: Int = playerHolder.shopPage

    override val maxItemsAmount: Int
        get() = shopComponent.model.value.maxItemsAmount

    override val nextPageButton: InventorySlot = buttonsRenderer.nextButton
    override val prevPageButton: InventorySlot = buttonsRenderer.prevButton
    override val backPageButton: InventorySlot = buttonsRenderer.backButton {
        val cleanPlayerHolder = ShopPlayerHolder(playerHolder.player)
        val route = GuiRouter.Route.Shops(cleanPlayerHolder)
        menuScope.launch(Dispatchers.IO) { router.open(route) }
    }

    override fun onInventoryClicked(e: InventoryClickEvent) {
        super.onInventoryClicked(e)
        e.isCancelled = true
    }

    override fun onPageChanged() {
        render()
    }

    override fun onCreated() {
        shopComponent.model
            .onEach { render() }
            .launchIn(menuScope)
    }

    private fun renderItemList(items: Map<String, ShopConfig.ShopItem>) {
        for (i in 0 until maxItemsPerPage) {
            val index = maxItemsPerPage * page + i
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
                .setOnClickListener {
                    val isValid = it.isLeftClick && !it.isShiftClick && shopComponent.model.value is Model.List
                    if (!isValid) return@setOnClickListener
                    val route = GuiRouter.Route.Buy(
                        playerHolder = playerHolder.copy(shopPage = page),
                        shopConfig = shopConfig,
                        shopItem = item
                    )
                    menuScope.launch(Dispatchers.IO) { router.open(route) }
                }.build().setInventorySlot()
        }
    }

    override fun render() {
        super.render()
        when (val state = shopComponent.model.value) {
            is Model.List -> {
                renderItemList(state.items)
            }

            Model.Loading -> Unit
        }
    }
}
