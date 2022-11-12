package ru.astrainteractive.astrashop.gui.shop

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.ChatColor
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
import ru.astrainteractive.astrashop.gui.buy.BuyGUI
import ru.astrainteractive.astrashop.gui.shops.ShopsGUI
import ru.astrainteractive.astrashop.modules.TranslationModule
import ru.astrainteractive.astrashop.utils.toItemStack
import ru.astrainteractive.astrashop.utils.withMeta


class ShopGUI(private val shopConfig: ShopConfig, player: Player) : PaginatedMenu(), IClickablePaginated {
    override var clicks: HashMap<Int, (InventoryClickEvent) -> Unit> = HashMap()

    private val pagingProvider = object : PagingProvider {
        override val page: Int
            get() = this@ShopGUI.page
        override val maxItemsPerPage: Int
            get() = this@ShopGUI.maxItemsPerPage

    }
    private val viewModel = ShopViewModel(shopConfig.configName, pagingProvider)

    override val playerMenuUtility: IPlayerHolder = object : IPlayerHolder {
        override val player: Player = player
    }

    override val backPageButton: IInventoryButton = BackButton {
        lifecycleScope.launch(Dispatchers.IO) {
            ShopsGUI(player).open()
        }
    }
    override val nextPageButton: IInventoryButton = NextButton
    override val prevPageButton: IInventoryButton = PrevButton
    private val translation by TranslationModule
    override val maxItemsAmount: Int
        get() = viewModel.state.value.items.keys.mapNotNull { it.toIntOrNull() }.maxOrNull() ?: 0
    override val menuSize: AstraMenuSize = AstraMenuSize.XL
    override val maxItemsPerPage: Int = menuSize.size - AstraMenuSize.XXS.size
    override var menuTitle: String = shopConfig.options.title
    override var page: Int = 0

    val myClickDetector = DSLEvent.event(InventoryClickEvent::class.java, inventoryEventHandler) { e ->
        e.isCancelled = true
        viewModel.onClicked(e)
    }

    override fun onInventoryClicked(e: InventoryClickEvent) {
        super.onInventoryClicked(e)
        e.isCancelled = true
        handleClick(e)
    }

    private fun onItemClicked(it: ShopConfig.ShopItem) = lifecycleScope.launch(Dispatchers.IO) {
        BuyGUI(shopConfig, it, playerMenuUtility.player).open()
    }

    override fun onInventoryClose(it: InventoryCloseEvent) {
        viewModel.clear()
    }

    override fun onPageChanged() {
        render(viewModel.state.value)
    }

    override fun onCreated() {
        viewModel.state.collectOn {
            println("Collected")
            render(it)
        }
    }

    private fun render(state: ShopListState) {
        inventory.clear()
        setManageButtons()
        rememberClick(backPageButton)
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

    private fun renderEditModeButton() {
        val itemStack = ItemStack(Material.BARRIER).withMeta {
            setDisplayName(translation.buttonEditMode)
            lore = listOf(translation.buttonEditModeExit)
        }
        button(prevPageButton.index + 1,itemStack){
            viewModel.exitEditMode()
        }.also(::rememberClick).set(inventory)
    }

    private fun renderItemList(items: Map<String, ShopConfig.ShopItem>) {
        for (i in 0 until maxItemsPerPage) {
            val index = maxItemsPerPage * page + i
            val item = items[index.toString()] ?: continue
            val itemStack = item.toItemStack().withMeta {
                lore = listOf(
                    translation.shopInfoStock.replace("{stock}",item.stock.toString()),
                    translation.shopInfoPrice.replace("{price}",item.price.toString()),
                    if (viewModel.state.value !is ShopListState.ListEditMode) translation.menuEdit else "",
                )
            }
            button(i, itemStack) {
                if (it.isLeftClick && !it.isShiftClick && viewModel.state.value is ShopListState.List)
                    onItemClicked(item)
            }.also(::rememberClick).set(inventory)
        }
    }

}