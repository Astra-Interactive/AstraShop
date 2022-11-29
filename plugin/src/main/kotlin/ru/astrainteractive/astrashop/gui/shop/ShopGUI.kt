package ru.astrainteractive.astrashop.gui.shop

import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.events.DSLEvent
import ru.astrainteractive.astralibs.menu.*
import ru.astrainteractive.astrashop.domain.models.ShopConfig
import ru.astrainteractive.astrashop.gui.*
import ru.astrainteractive.astrashop.gui.shop.state.ShopIntent
import ru.astrainteractive.astrashop.gui.shop.state.ShopListState
import ru.astrainteractive.astrashop.modules.TranslationModule
import ru.astrainteractive.astrashop.utils.Permission
import ru.astrainteractive.astrashop.utils.toItemStack
import ru.astrainteractive.astrashop.utils.withMeta


class ShopGUI(private val shopConfig: ShopConfig, override val playerMenuUtility: PlayerHolder) : PaginatedMenu(), PagingProvider {

    private val translation by TranslationModule
    private val viewModel = ShopViewModel(shopConfig.configName, this)
    private val clickListener = ClickListener()

    override val menuSize: AstraMenuSize = AstraMenuSize.XL
    override var menuTitle: String = shopConfig.options.title
    override var page: Int
        get() = playerMenuUtility.shopPage
        set(value){
            playerMenuUtility.shopPage = value
        }
    override val maxItemsPerPage: Int = menuSize.size - AstraMenuSize.XXS.size
    override val maxItemsAmount: Int
        get() = viewModel.maxItemsAmount

    override val nextPageButton: IInventoryButton = NextButton
    override val prevPageButton: IInventoryButton = PrevButton
    override val backPageButton: IInventoryButton = BackButton {
        viewModel.onIntent(ShopIntent.OpenShops(playerMenuUtility))
    }


    val myClickDetector = DSLEvent.event(InventoryClickEvent::class.java, inventoryEventHandler) { e ->
        e.isCancelled = true
        if (!listOf(prevPageButton.index + 1,backPageButton.index,prevPageButton.index,nextPageButton.index).contains(e.slot))
            ShopIntent.InventoryClick(e).also(viewModel::onIntent)
    }

    override fun onInventoryClicked(e: InventoryClickEvent) {
        super.onInventoryClicked(e)
        e.isCancelled = true
        clickListener.handle(e)
        if (Permission.EditShop.hasPermission(playerMenuUtility.player))
            viewModel.onIntent(ShopIntent.DeleteItem(e, e.isRightClick, e.isShiftClick))
    }

    override fun onInventoryClose(it: InventoryCloseEvent) {
        viewModel.clear()
    }

    override fun onPageChanged() {
        render()
    }

    override fun onCreated() {
        viewModel.state.collectOn(block = ::render)
    }


    private fun renderEditModeButton() {
        if (!Permission.EditShop.hasPermission(playerMenuUtility.player)) return
        val itemStack = when (viewModel.state.value) {
            is ShopListState.Loading, is ShopListState.List -> ItemStack(Material.LIGHT).withMeta {
                setDisplayName(translation.buttonEditModeDisabled)
                lore = listOf(translation.buttonEditModeEnter)
            }

            is ShopListState.ListEditMode -> ItemStack(Material.BARRIER).withMeta {
                setDisplayName(translation.buttonEditModeEnabled)
                lore = listOf(translation.buttonEditModeExit)
            }
        }


        button(prevPageButton.index + 1, itemStack) {
            if (Permission.EditShop.hasPermission(playerMenuUtility.player))
                viewModel.onIntent(ShopIntent.ToggleEditModeClick)
        }.also(clickListener::remember).set(inventory)
    }

    private fun renderItemList(items: Map<String, ShopConfig.ShopItem>) {
        for (i in 0 until maxItemsPerPage) {
            val index = maxItemsPerPage * page + i
            val item = items[index.toString()] ?: continue
            val itemStack = item.toItemStack().withMeta {
                lore = listOf(
                    translation.shopInfoStock(item.stock),
                    translation.shopInfoPrice(item.price),
                    translation.menuDeleteItem,
                    if (viewModel.state.value !is ShopListState.ListEditMode) translation.menuEdit else "",
                )
            }
            button(i, itemStack) {
                ShopIntent.OpenBuyGui(
                    shopConfig, item, playerMenuUtility,
                    it.isLeftClick, it.isShiftClick, viewModel.state.value
                ).let(viewModel::onIntent)
            }.also(clickListener::remember).set(inventory)
        }
    }

    private fun render(state: ShopListState = viewModel.state.value) {
        inventory.clear()
        clickListener.clear()
        setManageButtons()
        clickListener.remember(backPageButton)
        when (state) {
            is ShopListState.ListEditMode -> {
                renderEditModeButton()
                renderItemList(state.items)
            }

            is ShopListState.List -> {
                renderEditModeButton()
                renderItemList(state.items)
            }

            ShopListState.Loading -> {}
        }
    }

}

