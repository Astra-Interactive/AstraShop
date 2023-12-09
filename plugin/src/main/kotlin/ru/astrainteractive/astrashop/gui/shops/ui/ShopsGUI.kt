package ru.astrainteractive.astrashop.gui.shops.ui

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
import ru.astrainteractive.astralibs.menu.menu.setIndex
import ru.astrainteractive.astralibs.menu.menu.setItemStack
import ru.astrainteractive.astralibs.menu.menu.setOnClickListener
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astrashop.core.PluginTranslation
import ru.astrainteractive.astrashop.domain.util.ItemStackExt.toItemStack
import ru.astrainteractive.astrashop.gui.model.ShopPlayerHolder
import ru.astrainteractive.astrashop.gui.router.GuiRouter
import ru.astrainteractive.astrashop.gui.shops.presentation.ShopsComponent
import ru.astrainteractive.astrashop.gui.shops.presentation.ShopsComponent.Model
import ru.astrainteractive.astrashop.gui.util.Buttons
import ru.astrainteractive.astrashop.gui.util.ItemStackGuiExt.inventoryIndex

class ShopsGUI(
    override val playerHolder: ShopPlayerHolder,
    private val shopsComponent: ShopsComponent,
    translation: PluginTranslation,
    translationContext: BukkitTranslationContext,
    private val router: GuiRouter
) : PaginatedMenu() {
    private val buttons = Buttons(
        translation = translation,
        translationContext = translationContext,
        menu = this
    )

    override val menuSize: MenuSize = MenuSize.XL

    override var menuTitle: Component = with(translationContext) {
        translation.menu.menuTitle.toComponent()
    }
    override var page: Int
        get() = playerHolder.shopsPage
        set(value) {
            playerHolder.shopsPage = value
        }
    override val maxItemsPerPage: Int = menuSize.size - MenuSize.XXS.size
    override val maxItemsAmount: Int
        get() = shopsComponent.model.value.maxItemsAmount

    override val nextPageButton: InventorySlot = buttons.nextButton
    override val prevPageButton: InventorySlot = buttons.prevButton
    override val backPageButton: InventorySlot = buttons.backButton {
        inventory.close()
    }

    override fun onInventoryClicked(e: InventoryClickEvent) {
        super.onInventoryClicked(e)
        e.isCancelled = true
    }

    override fun onInventoryClose(it: InventoryCloseEvent) {
        shopsComponent.cancel()
        close()
    }

    override fun onPageChanged() = render()

    override fun onCreated() {
        shopsComponent.loadShops()
        shopsComponent.model
            .onEach { render() }
            .launchIn(componentScope)
    }

    private fun renderLoadedState(state: Model.Loaded) {
        for (i in 0 until maxItemsPerPage) {
            val index = inventoryIndex(i)
            val item = state.shops.getOrNull(index) ?: continue
            InventorySlot.Builder()
                .setIndex(i)
                .setItemStack(item.options.titleItem.toItemStack())
                .setOnClickListener {
                    componentScope.launch(Dispatchers.IO) {
                        val route = GuiRouter.Route.Shop(
                            playerHolder = playerHolder.copy(shopPage = 0),
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
