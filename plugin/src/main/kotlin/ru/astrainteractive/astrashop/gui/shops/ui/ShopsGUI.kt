package ru.astrainteractive.astrashop.gui.shops.ui

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
import ru.astrainteractive.astralibs.menu.inventory.util.PageContextExt.isFirstPage
import ru.astrainteractive.astralibs.menu.inventory.util.PageContextExt.isLastPage
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setIndex
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setItemStack
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setOnClickListener
import ru.astrainteractive.astrashop.core.PluginTranslation
import ru.astrainteractive.astrashop.domain.util.ItemStackExt.toItemStack
import ru.astrainteractive.astrashop.gui.model.ShopPlayerHolder
import ru.astrainteractive.astrashop.gui.renderer.ButtonsRenderer
import ru.astrainteractive.astrashop.gui.router.GuiRouter
import ru.astrainteractive.astrashop.gui.shops.presentation.ShopsComponent
import ru.astrainteractive.astrashop.gui.shops.presentation.ShopsComponent.Model

class ShopsGUI(
    override val playerHolder: ShopPlayerHolder,
    private val shopsComponent: ShopsComponent,
    translation: PluginTranslation,
    kyoriComponentSerializer: KyoriComponentSerializer,
    private val router: GuiRouter
) : PaginatedInventoryMenu(), KyoriComponentSerializer by kyoriComponentSerializer {
    override val childComponents: List<CoroutineScope> = listOf(shopsComponent)
    private val buttonsRenderer = ButtonsRenderer(
        translation = translation,
        menu = this,
        kyoriComponentSerializer = kyoriComponentSerializer
    )

    override val inventorySize: InventorySize = InventorySize.XL

    override var title: Component = translation.menu.menuTitle.let(::toComponent)
    override var pageContext: PageContext = PageContext(
        page = playerHolder.shopsPage,
        maxItems = shopsComponent.model.value.maxItemsAmount,
        maxItemsPerPage = inventorySize.size - InventorySize.XXS.size
    )

    override val nextPageButton: InventorySlot = buttonsRenderer.nextButton
    override val prevPageButton: InventorySlot = buttonsRenderer.prevButton
    private val backPageButton: InventorySlot = buttonsRenderer.backButton {
        inventory.close()
    }

    override fun onInventoryClicked(e: InventoryClickEvent) {
        super.onInventoryClicked(e)
        e.isCancelled = true
    }

    override fun onInventoryCreated() {
        shopsComponent.loadShops()
        shopsComponent.model
            .onEach { pageContext = pageContext.copy(maxItems = shopsComponent.model.value.maxItemsAmount) }
            .onEach { render() }
            .launchIn(menuScope)
    }

    private fun renderLoadedState(state: Model.Loaded) {
        state.shops.filter { it.options.page == pageContext.page }.forEach { shop ->
            InventorySlot.Builder()
                .setIndex(shop.options.index)
                .setItemStack(shop.options.titleItem.toItemStack())
                .setOnClickListener {
                    menuScope.launch(Dispatchers.IO) {
                        val route = GuiRouter.Route.Shop(
                            playerHolder = playerHolder.copy(shopPage = 0, shopsPage = pageContext.page),
                            shopConfig = shop
                        )
                        router.open(route)
                    }
                }.build().setInventorySlot()
        }
    }

    override fun render() {
        super.render()
        backPageButton.setInventorySlot()
        if (!pageContext.isLastPage) nextPageButton.setInventorySlot()
        if (!pageContext.isFirstPage) prevPageButton.setInventorySlot()
        when (val state: Model = shopsComponent.model.value) {
            is Model.Loaded -> renderLoadedState(state)
            Model.Loading -> Unit
        }
    }
}
