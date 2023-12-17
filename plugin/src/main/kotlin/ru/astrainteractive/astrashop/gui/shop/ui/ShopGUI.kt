package ru.astrainteractive.astrashop.gui.shop.ui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.menu.menu.MenuSize
import ru.astrainteractive.astralibs.menu.menu.PaginatedMenu
import ru.astrainteractive.astralibs.menu.menu.addLore
import ru.astrainteractive.astralibs.menu.menu.setIndex
import ru.astrainteractive.astralibs.menu.menu.setItemStack
import ru.astrainteractive.astralibs.menu.menu.setOnClickListener
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.core.PluginTranslation
import ru.astrainteractive.astrashop.domain.usecase.PriceCalculator
import ru.astrainteractive.astrashop.domain.util.ItemStackExt.toItemStack
import ru.astrainteractive.astrashop.gui.model.ShopPlayerHolder
import ru.astrainteractive.astrashop.gui.router.GuiRouter
import ru.astrainteractive.astrashop.gui.shop.presentation.ShopComponent
import ru.astrainteractive.astrashop.gui.shop.presentation.ShopComponent.Model
import ru.astrainteractive.astrashop.gui.util.Buttons
import ru.astrainteractive.astrashop.gui.util.RoundExt.round

@Suppress("LongParameterList")
class ShopGUI(
    private val shopConfig: ShopConfig,
    override val playerHolder: ShopPlayerHolder,
    private val translation: PluginTranslation,
    private val router: GuiRouter,
    private val shopComponent: ShopComponent,
    translationContext: BukkitTranslationContext
) : PaginatedMenu(), BukkitTranslationContext by translationContext {
    private val buttons = Buttons(
        translation = translation,
        translationContext = translationContext,
        menu = this
    )

    override val menuSize: MenuSize = MenuSize.XL
    override var menuTitle: Component = shopConfig.options.title.toComponent()
    override var page: Int = playerHolder.shopPage

    override val maxItemsAmount: Int
        get() = shopComponent.model.value.maxItemsAmount

    override val nextPageButton: InventorySlot = buttons.nextButton
    override val prevPageButton: InventorySlot = buttons.prevButton
    override val backPageButton: InventorySlot = buttons.backButton {
        val cleanPlayerHolder = ShopPlayerHolder(playerHolder.player)
        val route = GuiRouter.Route.Shops(cleanPlayerHolder)
        componentScope.launch(Dispatchers.IO) { router.open(route) }
    }

    override fun onInventoryClicked(e: InventoryClickEvent) {
        super.onInventoryClicked(e)
        e.isCancelled = true
    }

    override fun onInventoryClose(it: InventoryCloseEvent) {
        shopComponent.cancel()
        close()
    }

    override fun onPageChanged() {
        render()
    }

    override fun onCreated() {
        shopComponent.model
            .onEach { render() }
            .launchIn(componentScope)
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
                .addLore(translation.buttons.shopInfoStock(item.stock).toComponent())
                .addLore(translation.buttons.shopInfoBuyPrice(buyPrice).toComponent())
                .addLore(translation.buttons.shopInfoSellPrice(sellPrice).toComponent())
                .addLore(translation.menu.menuDeleteItem.toComponent())
                .setOnClickListener {
                    val isValid = it.isLeftClick && !it.isShiftClick && shopComponent.model.value is Model.List
                    if (!isValid) return@setOnClickListener
                    val route = GuiRouter.Route.Buy(
                        playerHolder = playerHolder.copy(shopPage = page),
                        shopConfig = shopConfig,
                        shopItem = item
                    )
                    componentScope.launch(Dispatchers.IO) { router.open(route) }
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
