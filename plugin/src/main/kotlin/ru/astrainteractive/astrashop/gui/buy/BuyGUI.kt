package ru.astrainteractive.astrashop.gui.buy

import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.menu.*
import ru.astrainteractive.astrashop.asState
import ru.astrainteractive.astrashop.domain.models.ShopConfig
import ru.astrainteractive.astrashop.gui.*
import ru.astrainteractive.astrashop.modules.DataSourceModule
import ru.astrainteractive.astrashop.modules.TranslationModule
import ru.astrainteractive.astrashop.utils.copy
import ru.astrainteractive.astrashop.utils.toItemStack
import ru.astrainteractive.astrashop.utils.withMeta
import kotlin.math.pow


class BuyGUI(shopConfig: ShopConfig, item: ShopConfig.ShopItem, player: Player) : Menu(), IClickablePaginated {

    override var clicks: HashMap<Int, (InventoryClickEvent) -> Unit> = HashMap()
    private val dataSource by DataSourceModule
    private val viewModel = BuyViewModel(shopConfig.configName, item.itemIndex, player)
    private val translation by TranslationModule
    override val playerMenuUtility: IPlayerHolder = object : IPlayerHolder {
        override val player: Player = player
    }

    private val balanceButton :IInventoryButton
        get() =  BalanceButton(viewModel.state.value.asState<BuyState.Loaded>())
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

    private enum class Type {
        BUY, SELL
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
                    val itemStack = this
                    itemStack.withMeta {
                        this.setDisplayName(translation.buttonBuyAmount.replace("{amount}",itemStack.amount.toString()))
                        val totalPrice = (itemStack.amount*buyState.item.price).toString()
                        this.lore = listOf(
                            translation.shopInfoPrice.replace("{price}",totalPrice)
                        )
                    }
                }
                setItemList(11, buyState) {
                    val itemStack = this
                    itemStack.withMeta {
                        this.setDisplayName(translation.buttonSellAmount.replace("{amount}",itemStack.amount.toString()))
                        val totalPrice = (itemStack.amount*buyState.item.price).toString()
                        this.lore = listOf(
                            translation.shopInfoPrice.replace("{price}",totalPrice)
                        )
                    }
                }
            }

            BuyState.Loading -> {}
        }

    }


}