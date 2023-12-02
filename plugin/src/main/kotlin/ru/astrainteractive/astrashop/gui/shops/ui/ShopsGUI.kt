package ru.astrainteractive.astrashop.gui.shops.ui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import ru.astrainteractive.astralibs.menu.clicker.MenuClickListener
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.menu.menu.MenuSize
import ru.astrainteractive.astralibs.menu.menu.PaginatedMenu
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astrashop.core.PluginTranslation
import ru.astrainteractive.astrashop.gui.model.ShopPlayerHolder
import ru.astrainteractive.astrashop.gui.shops.presentation.ShopsComponent
import ru.astrainteractive.astrashop.gui.shops.presentation.ShopsComponent.Model
import ru.astrainteractive.astrashop.gui.util.Buttons
import ru.astrainteractive.astrashop.util.inventoryIndex
import ru.astrainteractive.astrashop.util.toItemStack

class ShopsGUI(
    override val playerHolder: ShopPlayerHolder,
    private val shopsComponent: ShopsComponent,
    translation: PluginTranslation,
    translationContext: BukkitTranslationContext,
) : PaginatedMenu(), BukkitTranslationContext by translationContext {

    private val buttons = Buttons(
        lifecycleScope = this,
        translation = translation,
        translationContext = translationContext,
        menu = this
    )

    private val clickListener = MenuClickListener()

    override val menuSize: MenuSize = MenuSize.XL
    override var menuTitle: Component = translation.menu.menuTitle.toComponent()
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
        e.isCancelled = true
        clickListener.onClick(e)
    }

    override fun onInventoryClose(it: InventoryCloseEvent) {
        shopsComponent.cancel()
        close()
    }

    override fun onPageChanged() = render()

    override fun onCreated() {
        shopsComponent.model.collectOn(Dispatchers.IO, block = ::render)
    }

    private fun renderLoadedState(state: Model.Loaded) {
        for (i in 0 until maxItemsPerPage) {
            val index = inventoryIndex(i)
            val item = state.shops.getOrNull(index) ?: continue
            buttons.button(i, item.options.titleItem.toItemStack()) {
                componentScope.launch(Dispatchers.IO) {
                    TODO()
//                    ShopGUI(item, playerHolder.copy(shopPage = 0))
                }
            }.also(clickListener::remember).setInventorySlot()
        }
    }

    private fun render(state: Model = shopsComponent.model.value) {
        inventory.clear()
        clickListener.clearClickListener()
        clickListener.remember(backPageButton)
        setManageButtons(clickListener)

        when (state) {
            is Model.Loaded -> renderLoadedState(state)
            Model.Loading -> Unit
        }
    }
}
