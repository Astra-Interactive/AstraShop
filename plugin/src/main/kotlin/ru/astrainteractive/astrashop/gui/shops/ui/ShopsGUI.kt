package ru.astrainteractive.astrashop.gui.shops.ui

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
import ru.astrainteractive.astralibs.menu.menu.setIndex
import ru.astrainteractive.astralibs.menu.menu.setItemStack
import ru.astrainteractive.astralibs.menu.menu.setOnClickListener
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
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
) : PaginatedMenu(), KyoriComponentSerializer by kyoriComponentSerializer {
    override val childComponents: List<CoroutineScope> = listOf(shopsComponent)
    private val buttonsRenderer = ButtonsRenderer(
        translation = translation,
        menu = this,
        kyoriComponentSerializer = kyoriComponentSerializer
    )

    override val menuSize: MenuSize = MenuSize.XL

    override var menuTitle: Component = translation.menu.menuTitle.let(::toComponent)
    override var page: Int = playerHolder.shopsPage
    override val maxItemsPerPage: Int = menuSize.size - MenuSize.XXS.size
    override val maxItemsAmount: Int
        get() = shopsComponent.model.value.maxItemsAmount

    override val nextPageButton: InventorySlot = buttonsRenderer.nextButton
    override val prevPageButton: InventorySlot = buttonsRenderer.prevButton
    override val backPageButton: InventorySlot = buttonsRenderer.backButton {
        inventory.close()
    }

    override fun onInventoryClicked(e: InventoryClickEvent) {
        super.onInventoryClicked(e)
        e.isCancelled = true
    }

    override fun onPageChanged() = render()

    override fun onCreated() {
        shopsComponent.loadShops()
        shopsComponent.model
            .onEach { render() }
            .launchIn(menuScope)
    }

    private fun renderLoadedState(state: Model.Loaded) {
        for (i in 0 until maxItemsPerPage) {
            val index = i + maxItemsPerPage * page
            val item = state.shops.getOrNull(index) ?: continue
            InventorySlot.Builder()
                .setIndex(i)
                .setItemStack(item.options.titleItem.toItemStack())
                .setOnClickListener {
                    menuScope.launch(Dispatchers.IO) {
                        val route = GuiRouter.Route.Shop(
                            playerHolder = playerHolder.copy(shopPage = 0, shopsPage = page),
                            shopConfig = item
                        )
                        router.open(route)
                    }
                }.build().setInventorySlot()
        }
    }

    override fun render() {
        super.render()
        when (val state: Model = shopsComponent.model.value) {
            is Model.Loaded -> renderLoadedState(state)
            Model.Loading -> Unit
        }
    }
}
