package ru.astrainteractive.astrashop.gui.shops

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import ru.astrainteractive.astralibs.menu.clicker.MenuClickListener
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.menu.menu.MenuSize
import ru.astrainteractive.astralibs.menu.menu.PaginatedMenu
import ru.astrainteractive.astrashop.di.impl.RootModuleImpl
import ru.astrainteractive.astrashop.gui.shop.ShopGUI
import ru.astrainteractive.astrashop.gui.util.BackButton
import ru.astrainteractive.astrashop.gui.util.NextButton
import ru.astrainteractive.astrashop.gui.util.PrevButton
import ru.astrainteractive.astrashop.gui.util.ShopPlayerHolder
import ru.astrainteractive.astrashop.gui.util.button
import ru.astrainteractive.astrashop.util.inventoryIndex
import ru.astrainteractive.astrashop.util.openOnMainThread
import ru.astrainteractive.astrashop.util.toItemStack
import ru.astrainteractive.klibs.kdi.getValue

class ShopsGUI(override val playerHolder: ShopPlayerHolder) : PaginatedMenu() {

    private val translation by RootModuleImpl.translation

    private val shopsComponent = DefaultShopsComponent()
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
        get() = shopsComponent.model.value.maxItemsAmount

    override val nextPageButton: InventorySlot = NextButton
    override val prevPageButton: InventorySlot = PrevButton
    override val backPageButton: InventorySlot = BackButton {
        inventory.close()
    }

    override fun onInventoryClicked(e: InventoryClickEvent) {
        e.isCancelled = true
        clickListener.onClick(e)
    }

    override fun onInventoryClose(it: InventoryCloseEvent) {
        shopsComponent.close()
    }

    override fun onPageChanged() = render()

    override fun onCreated() {
        shopsComponent.model.collectOn(Dispatchers.IO, block = ::render)
    }

    private fun renderLoadedState(state: ShopsComponent.Model.Loaded) {
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

    private fun render(state: ShopsComponent.Model = shopsComponent.model.value) {
        inventory.clear()
        clickListener.clearClickListener()
        clickListener.remember(backPageButton)
        setManageButtons(clickListener)

        when (state) {
            is ShopsComponent.Model.Loaded -> renderLoadedState(state)
            ShopsComponent.Model.Loading -> Unit
        }
    }
}
