package ru.astrainteractive.astrashop.gui.shop

import org.bukkit.Material
import org.bukkit.entity.Player
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
import ru.astrainteractive.astrashop.utils.toItemStack
import ru.astrainteractive.astrashop.utils.withMeta


class ShopGUI(private val shopConfig: ShopConfig, player: Player) : PaginatedMenu(), PagingProvider {

    private val translation by TranslationModule
    private val viewModel = ShopViewModel(shopConfig.configName, this)
    private val clickListener = ClickListener()

    override val menuSize: AstraMenuSize = AstraMenuSize.XL
    override var menuTitle: String = shopConfig.options.title
    override var page: Int = 0
    override val maxItemsPerPage: Int = menuSize.size - AstraMenuSize.XXS.size
    override val maxItemsAmount: Int
        get() = viewModel.maxItemsAmount

    override val playerMenuUtility = PlayerHolder(player)

    override val nextPageButton: IInventoryButton = NextButton
    override val prevPageButton: IInventoryButton = PrevButton
    override val backPageButton: IInventoryButton = BackButton {
        viewModel.onIntent(ShopIntent.OpenShops(playerMenuUtility))
    }


    val myClickDetector = DSLEvent.event(InventoryClickEvent::class.java, inventoryEventHandler) { e ->
        e.isCancelled = true
        viewModel.onIntent(ShopIntent.EditModeClick(e))
    }

    override fun onInventoryClicked(e: InventoryClickEvent) {
        super.onInventoryClicked(e)
        e.isCancelled = true
        clickListener.handle(e)
    }

    override fun onInventoryClose(it: InventoryCloseEvent) {
        viewModel.clear()
    }

    override fun onPageChanged() {
        render(viewModel.state.value)
    }

    override fun onCreated() {
        viewModel.state.collectOn(block = ::render)
    }


    private fun renderEditModeButton() {
        val itemStack = ItemStack(Material.BARRIER).withMeta {
            setDisplayName(translation.buttonEditMode)
            lore = listOf(translation.buttonEditModeExit)
        }
        button(prevPageButton.index + 1, itemStack) {
            viewModel.onIntent(ShopIntent.ExitEditMode)
        }.also(clickListener::remember).set(inventory)
    }

    private fun renderItemList(items: Map<String, ShopConfig.ShopItem>) {
        for (i in 0 until maxItemsPerPage) {
            val index = maxItemsPerPage * page + i
            val item = items[index.toString()] ?: continue
            val itemStack = item.toItemStack().withMeta {
                lore = listOf(
                    translation.shopInfoStock(item.stock),
                    translation.shopInfoPrice(item.price.toInt()),
                    if (viewModel.state.value !is ShopListState.ListEditMode) translation.menuEdit else "",
                )
            }
            button(i, itemStack) {
                ShopIntent.OpenBuyGui(
                    shopConfig, item, playerMenuUtility,
                    it.isLeftClick, it.isShiftClick, viewModel.state.value
                ).also(viewModel::onIntent)
            }.also(clickListener::remember).set(inventory)
        }
    }

    private fun render(state: ShopListState = viewModel.state.value) {
        inventory.clear()
        setManageButtons()
        clickListener.remember(backPageButton)
        when (state) {
            is ShopListState.ListEditMode -> {
                renderItemList(state.items)
                renderEditModeButton()
            }

            is ShopListState.List -> {
                renderItemList(state.items)
            }

            ShopListState.Loading -> {}
        }
    }

}

