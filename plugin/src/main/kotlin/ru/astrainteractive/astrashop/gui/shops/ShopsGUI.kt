package ru.astrainteractive.astrashop.gui.shops

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.menu.*
import ru.astrainteractive.astrashop.State
import ru.astrainteractive.astrashop.asState
import ru.astrainteractive.astrashop.domain.models.ShopConfig
import ru.astrainteractive.astrashop.gui.*
import ru.astrainteractive.astrashop.gui.shop.ShopGUI
import ru.astrainteractive.astrashop.modules.TranslationModule
import ru.astrainteractive.astrashop.utils.inventoryIndex
import ru.astrainteractive.astrashop.utils.toItemStack


class ShopsGUI(override val playerMenuUtility: PlayerHolder) : PaginatedMenu() {

    private val translation by TranslationModule
    private val viewModel = ShopsViewModel()
    private val clickListener = ClickListener()

    override val menuSize: AstraMenuSize = AstraMenuSize.XL
    override var menuTitle: String = translation.menuTitle
    override var page: Int
        get() = playerMenuUtility.shopsPage
        set(value){
            playerMenuUtility.shopsPage = value
        }
    override val maxItemsPerPage: Int = menuSize.size - AstraMenuSize.XXS.size
    override val maxItemsAmount: Int
        get() = viewModel.maxItemsAmount


    override val nextPageButton: IInventoryButton = NextButton
    override val prevPageButton: IInventoryButton = PrevButton
    override val backPageButton: IInventoryButton = BackButton {
        inventory.close()
    }


    override fun onInventoryClicked(e: InventoryClickEvent) {
        super.onInventoryClicked(e)
        e.isCancelled = true
        clickListener.handle(e)
    }

    override fun onInventoryClose(it: InventoryCloseEvent) {
        viewModel.clear()
    }

    override fun onPageChanged() = render()

    override fun onCreated() {
        viewModel.state.collectOn(block = ::render)
    }


    private fun renderLoadedState(state: ShopsState.Loaded) {
        for (i in 0 until maxItemsPerPage) {
            val index = inventoryIndex(i)
            val item = state.shops.getOrNull(index) ?: continue
            button(i, item.options.titleItem.toItemStack()) {
                lifecycleScope.launch(Dispatchers.IO) {
                    ShopGUI(item, playerMenuUtility).open()
                }
            }.also(clickListener::remember).set(inventory)
        }
    }

    private fun render(state: ShopsState = viewModel.state.value) {
        inventory.clear()
        clickListener.clear()
        clickListener.remember(backPageButton)
        setManageButtons()

        when (state) {
            is ShopsState.Loaded -> renderLoadedState(state)
            ShopsState.Loading -> Unit
        }
    }
}