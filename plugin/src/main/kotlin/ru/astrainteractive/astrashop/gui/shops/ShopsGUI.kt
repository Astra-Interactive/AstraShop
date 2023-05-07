package ru.astrainteractive.astrashop.gui.shops

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import ru.astrainteractive.astralibs.getValue
import ru.astrainteractive.astralibs.menu.clicker.MenuClickListener
import ru.astrainteractive.astralibs.menu.menu.InventoryButton
import ru.astrainteractive.astralibs.menu.menu.MenuSize
import ru.astrainteractive.astralibs.menu.menu.PaginatedMenu
import ru.astrainteractive.astrashop.di.impl.RootModuleImpl
import ru.astrainteractive.astrashop.gui.BackButton
import ru.astrainteractive.astrashop.gui.NextButton
import ru.astrainteractive.astrashop.gui.PrevButton
import ru.astrainteractive.astrashop.gui.ShopPlayerHolder
import ru.astrainteractive.astrashop.gui.button
import ru.astrainteractive.astrashop.gui.shop.ShopGUI
import ru.astrainteractive.astrashop.utils.inventoryIndex
import ru.astrainteractive.astrashop.utils.openOnMainThread
import ru.astrainteractive.astrashop.utils.toItemStack

class ShopsGUI(override val playerHolder: ShopPlayerHolder) : PaginatedMenu() {

    private val translation by RootModuleImpl.translation

    private val viewModel = ShopsViewModel()
    private val clickListener = MenuClickListener()

    override val menuSize: MenuSize = MenuSize.XL
    override var menuTitle: String = translation.menuTitle
    override var page: Int
        get() = playerHolder.shopsPage
        set(value) {
            playerHolder.shopsPage = value
        }
    override val maxItemsPerPage: Int = menuSize.size - MenuSize.XXS.size
    override val maxItemsAmount: Int
        get() = viewModel.maxItemsAmount

    override val nextPageButton: InventoryButton = NextButton
    override val prevPageButton: InventoryButton = PrevButton
    override val backPageButton: InventoryButton = BackButton {
        inventory.close()
    }

    override fun onInventoryClicked(e: InventoryClickEvent) {
        e.isCancelled = true
        clickListener.onClick(e)
    }

    override fun onInventoryClose(it: InventoryCloseEvent) {
        viewModel.close()
    }

    override fun onPageChanged() = render()

    override fun onCreated() {
        viewModel.state.collectOn(Dispatchers.IO, block = ::render)
    }

    private fun renderLoadedState(state: ShopsState.Loaded) {
        for (i in 0 until maxItemsPerPage) {
            val index = inventoryIndex(i)
            val item = state.shops.getOrNull(index) ?: continue
            button(i, item.options.titleItem.toItemStack()) {
                componentScope.launch(Dispatchers.IO) {
                    ShopGUI(item, playerHolder.copy(shopPage = 0)).openOnMainThread()
                }
            }.also(clickListener::remember).setInventoryButton()
        }
    }

    private fun render(state: ShopsState = viewModel.state.value) {
        inventory.clear()
        clickListener.clearClickListener()
        clickListener.remember(backPageButton)
        setManageButtons(clickListener)

        when (state) {
            is ShopsState.Loaded -> renderLoadedState(state)
            ShopsState.Loading -> Unit
        }
    }
}
