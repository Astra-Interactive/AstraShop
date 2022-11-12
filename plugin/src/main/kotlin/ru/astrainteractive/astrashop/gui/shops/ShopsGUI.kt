package ru.astrainteractive.astrashop.gui.shops

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.menu.*
import ru.astrainteractive.astrashop.domain.models.ShopConfig
import ru.astrainteractive.astrashop.domain.models.SpigotTitleItem
import ru.astrainteractive.astrashop.gui.*
import ru.astrainteractive.astrashop.gui.shop.ShopGUI
import ru.astrainteractive.astrashop.modules.TranslationModule
import ru.astrainteractive.astrashop.utils.inventoryIndex
import ru.astrainteractive.astrashop.utils.toItemStack
import ru.astrainteractive.astrashop.utils.withMeta


class ShopsGUI(player: Player) : PaginatedMenu(), IClickablePaginated {
    override var clicks: HashMap<Int, (InventoryClickEvent) -> Unit> = HashMap()
    private val translation by TranslationModule
    private val viewModel = ShopsViewModel()

    override val playerMenuUtility: IPlayerHolder = object : IPlayerHolder {
        override val player: Player = player
    }

    override val backPageButton: IInventoryButton = BackButton{
        lifecycleScope.launch(Dispatchers.IO) {
            ShopsGUI(player).open()
        }
    }

    override val nextPageButton: IInventoryButton = NextButton
    override val prevPageButton: IInventoryButton = PrevButton
    override val maxItemsAmount: Int = viewModel.shops.size
    override val menuSize: AstraMenuSize = AstraMenuSize.XL
    override var menuTitle: String = translation.menuTitle
    override var page: Int = 0

    override fun onInventoryClicked(e: InventoryClickEvent) {
        super.onInventoryClicked(e)
        e.isCancelled = true
        handleClick(e)
    }


    override fun onInventoryClose(it: InventoryCloseEvent) {
        viewModel.clear()
    }

    override fun onPageChanged() = render()

    override fun onCreated() = render()

    fun render() {
        forgetClicks()
        setManageButtons()
        rememberClick(backPageButton)
        val shops = viewModel.shops

        for (i in 0 until maxItemsPerPage) {
            val index = inventoryIndex(i)
            val item = shops.getOrNull(index) ?: continue
            button(i, item.options.titleItem.toItemStack()) {
                lifecycleScope.launch(Dispatchers.IO) {
                    ShopGUI(item, playerMenuUtility.player).open()
                }
            }.also(::rememberClick).set(inventory)
        }
    }
}
