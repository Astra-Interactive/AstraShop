package ru.astrainteractive.astrashop.gui.shop

import kotlinx.coroutines.Dispatchers
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.menu.clicker.MenuClickListener
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.menu.menu.MenuSize
import ru.astrainteractive.astralibs.menu.menu.PaginatedMenu
import ru.astrainteractive.astrashop.api.calculator.PriceCalculator
import ru.astrainteractive.astrashop.api.util.SpigotShopConfigAlias
import ru.astrainteractive.astrashop.api.util.SpigotShopItemAlias
import ru.astrainteractive.astrashop.di.impl.RootModuleImpl
import ru.astrainteractive.astrashop.gui.shop.state.ShopIntent
import ru.astrainteractive.astrashop.gui.shop.state.ShopListState
import ru.astrainteractive.astrashop.gui.shop.util.PagingProvider
import ru.astrainteractive.astrashop.gui.util.BackButton
import ru.astrainteractive.astrashop.gui.util.NextButton
import ru.astrainteractive.astrashop.gui.util.PrevButton
import ru.astrainteractive.astrashop.gui.util.ShopPlayerHolder
import ru.astrainteractive.astrashop.gui.util.button
import ru.astrainteractive.astrashop.util.PluginPermission
import ru.astrainteractive.astrashop.util.toItemStack
import ru.astrainteractive.klibs.kdi.getValue

class ShopGUI(
    private val shopConfig: SpigotShopConfigAlias,
    override val playerHolder: ShopPlayerHolder
) : PaginatedMenu(), PagingProvider {
    private val translation by RootModuleImpl.translation

    private val viewModel = ShopViewModel(shopConfig.configName, this)
    private val clickListener = MenuClickListener()

    override val menuSize: MenuSize = MenuSize.XL
    override var menuTitle: String = shopConfig.options.title
    override var page: Int
        get() = playerHolder.shopPage
        set(value) {
            playerHolder.shopPage = value
        }
    override val maxItemsAmount: Int
        get() = viewModel.maxItemsAmount

    override val nextPageButton: InventorySlot = NextButton
    override val prevPageButton: InventorySlot = PrevButton
    override val backPageButton: InventorySlot = BackButton {
        viewModel.onIntent(ShopIntent.OpenShops(playerHolder))
    }

    override fun onInventoryClicked(e: InventoryClickEvent) {
        e.isCancelled = true
        clickListener.onClick(e)
        if (PluginPermission.EditShop.hasPermission(playerHolder.player)) {
            viewModel.onIntent(ShopIntent.DeleteItem(e, e.isRightClick, e.isShiftClick))
        }
        if (!listOf(
                prevPageButton.index + 1,
                backPageButton.index,
                prevPageButton.index,
                nextPageButton.index
            ).contains(e.slot)
        ) {
            ShopIntent.InventoryClick(e).also(viewModel::onIntent)
        }
    }

    override fun onInventoryClose(it: InventoryCloseEvent) {
        viewModel.close()
    }

    override fun onPageChanged() {
        render()
    }

    override fun onCreated() {
        viewModel.state.collectOn(Dispatchers.IO, block = ::render)
    }

    private fun renderEditModeButton() {
        if (!PluginPermission.EditShop.hasPermission(playerHolder.player)) return
        val itemStack = when (viewModel.state.value) {
            is ShopListState.Loading, is ShopListState.List -> ItemStack(Material.LIGHT).apply {
                editMeta {
                    it.setDisplayName(translation.buttonEditModeDisabled)
                }
                lore = listOf(translation.buttonEditModeEnter)
            }

            is ShopListState.ListEditMode -> ItemStack(Material.BARRIER).apply {
                editMeta {
                    it.setDisplayName(translation.buttonEditModeEnabled)
                }
                lore = listOf(translation.buttonEditModeExit)
            }
        }

        button(prevPageButton.index + 1, itemStack) {
            if (PluginPermission.EditShop.hasPermission(playerHolder.player)) {
                viewModel.onIntent(ShopIntent.ToggleEditModeClick)
            }
        }.also(clickListener::remember).setInventoryButton()
    }

    private fun renderItemList(items: Map<String, SpigotShopItemAlias>) {
        for (i in 0 until maxItemsPerPage) {
            val index = maxItemsPerPage * page + i
            val item = items[index.toString()] ?: continue
            val itemStack = item.toItemStack().apply {
                lore = listOf(
                    translation.shopInfoStock(item.stock),
                    translation.shopInfoPrice(PriceCalculator.calculateBuyPrice(item, 1)),
                    translation.shopInfoSellPrice(PriceCalculator.calculateSellPrice(item, 1)),
                    translation.menuDeleteItem,
                    if (viewModel.state.value !is ShopListState.ListEditMode) translation.menuEdit else "",
                )
            }
            button(i, itemStack) {
                ShopIntent.OpenBuyGui(
                    shopConfig,
                    item,
                    playerHolder,
                    it.isLeftClick,
                    it.isShiftClick,
                    viewModel.state.value
                ).also(viewModel::onIntent)
            }.also(clickListener::remember).setInventoryButton()
        }
    }

    private fun render(state: ShopListState = viewModel.state.value) {
        inventory.clear()
        clickListener.clearClickListener()
        setManageButtons(clickListener)
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
