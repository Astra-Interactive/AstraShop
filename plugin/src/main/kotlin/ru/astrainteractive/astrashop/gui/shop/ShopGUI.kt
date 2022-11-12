package ru.astrainteractive.astrashop.gui.shop

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.menu.*
import ru.astrainteractive.astrashop.domain.models.ShopConfig
import ru.astrainteractive.astrashop.domain.models.ShopItemStack
import ru.astrainteractive.astrashop.domain.models.ShopMaterial
import ru.astrainteractive.astrashop.gui.*
import ru.astrainteractive.astrashop.gui.buy.BuyGUI
import ru.astrainteractive.astrashop.gui.shops.ShopsGUI
import ru.astrainteractive.astrashop.utils.toItemStack
import ru.astrainteractive.astrashop.utils.withMeta


class ShopGUI(private val shopConfig: ShopConfig, player: Player) : PaginatedMenu(), IClickablePaginated {
    override var clicks: HashMap<Int, (InventoryClickEvent) -> Unit> = HashMap()

    private val viewModel = ShopViewModel(shopConfig)

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

    override val maxItemsAmount: Int = viewModel.items.keys.mapNotNull { it.toIntOrNull() }.maxOrNull() ?: 0
    override val menuSize: AstraMenuSize = AstraMenuSize.XL
    override val maxItemsPerPage: Int = menuSize.size - AstraMenuSize.XXS.size
    override var menuTitle: String = viewModel.shopConfig.options.title
    override var page: Int = 0

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
        render()
    }

    override fun onCreated() {
        render()
    }

    private fun render() {
        inventory.clear()
        setManageButtons()
        rememberClick(backPageButton)
        val items = viewModel.items

        for (i in 0 until maxItemsPerPage) {
            val index = maxItemsPerPage * page + i
            val item = items[index.toString()] ?: continue
            val itemStack = buildItem(item)
            button(i,itemStack){
                onItemClicked(item)
            }.also(::rememberClick).set(inventory)
        }

    }

    private fun buildItem(item: ShopConfig.ShopItem) = item.toItemStack().withMeta {
        lore = listOf(
            "${ChatColor.WHITE}Stock: ${item.stock}",
            "${ChatColor.WHITE}Median: ${item.median}",
            "${ChatColor.WHITE}Price: ${item.price}",
        )
    }
}