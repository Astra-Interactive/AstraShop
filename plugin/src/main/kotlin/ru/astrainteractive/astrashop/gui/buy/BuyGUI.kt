package ru.astrainteractive.astrashop.gui.buy

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.async.BukkitMain
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.menu.*
import ru.astrainteractive.astrashop.domain.models.ShopConfig
import ru.astrainteractive.astrashop.gui.*
import ru.astrainteractive.astrashop.modules.DataSourceModule
import ru.astrainteractive.astrashop.utils.copy
import ru.astrainteractive.astrashop.utils.toItemStack
import kotlin.math.pow


class BuyGUI(shopConfig: ShopConfig, item: ShopConfig.ShopItem, player: Player) : Menu(), IClickablePaginated {

    override var clicks: HashMap<Int, (InventoryClickEvent) -> Unit> = HashMap()
    private val dataSource by DataSourceModule
    private val viewModel = BuyViewModel(shopConfig.configName, item.itemIndex, player)

    override val playerMenuUtility: IPlayerHolder = object : IPlayerHolder {
        override val player: Player = player
    }

    private val balanceButton = BalanceButton(shopConfig)
    private val backButton = BackToShopButton(shopConfig, player, lifecycleScope)
    private val buyInfoButton = BuyInfoButton
    private val sellInfoButton = SellInfoButton

    override val menuSize: AstraMenuSize = AstraMenuSize.XS
    override var menuTitle: String = item.toItemStack().itemMeta.displayName.ifEmpty { item.toItemStack().type.name }


    override fun onCreated() {
        viewModel.state.collectOn {
            render(it)
        }
    }


    override fun onInventoryClicked(e: InventoryClickEvent) {
        e.isCancelled = true
        handleClick(e)
    }

    override fun onInventoryClose(it: InventoryCloseEvent) {
        viewModel.clear()
    }

    private fun setItemList(startIndex: Int, state: BuyState.Loaded, itemBuilder: ItemStack.() -> Unit) {
        val item = state.item
        IntRange(startIndex, startIndex + 6).forEach { j ->
            val i = j - startIndex
            val amount = 2.0.pow(i).toInt()
            if (item.stock != -1 && item.stock < amount) return@forEach
            val item = item.toItemStack().copy(amount).apply(itemBuilder)
            button(j, item) {
                if (startIndex == 2)
                    viewModel.onBuyClicked(amount)
                else
                    viewModel.onSellClicked(amount)
            }.also(::rememberClick).set(inventory)
        }
    }

    private fun render(buyState: BuyState) {
        inventory.clear()
        when (buyState) {
            is BuyState.Loaded -> {
                forgetClicks()
                rememberClick(balanceButton)
                rememberClick(backButton)
                balanceButton.set(inventory)
                backButton.set(inventory)
                buyInfoButton.set(inventory)
                sellInfoButton.set(inventory)

                setItemList(2, buyState) {

                }
                setItemList(11, buyState) {

                }
            }

            BuyState.Loading -> {}
        }

    }


}