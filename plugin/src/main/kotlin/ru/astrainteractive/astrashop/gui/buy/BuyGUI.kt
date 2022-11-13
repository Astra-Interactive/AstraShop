package ru.astrainteractive.astrashop.gui.buy

import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
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


class BuyGUI(shopConfig: ShopConfig, item: ShopConfig.ShopItem, player: Player) : Menu() {

    private val viewModel = BuyViewModel(shopConfig.configName, item.itemIndex, player)
    private val translation by TranslationModule
    private val clickListener = ClickListener()

    override val menuSize: AstraMenuSize = AstraMenuSize.XS
    override var menuTitle: String = item.toItemStack().itemMeta.displayName.ifEmpty { item.toItemStack().type.name }

    override val playerMenuUtility = PlayerHolder(player)

    private val backButton = BackToShopButton(shopConfig, player, lifecycleScope)
    private val buyInfoButton = BuyInfoButton
    private val sellInfoButton = SellInfoButton
    private val balanceButton: IInventoryButton
        get() = BalanceButton(viewModel.state.value.asState<BuyState.Loaded>())

    override fun onCreated() {
        viewModel.state.collectOn(block = ::render)
    }


    override fun onInventoryClicked(e: InventoryClickEvent) {
        e.isCancelled = true
        clickListener.handle(e)
    }

    override fun onInventoryClose(it: InventoryCloseEvent) {
        viewModel.clear()
    }



    private fun setActionButton(type: BuyType, i: Int, state: BuyState.Loaded) {
        val amount = 2.0.pow(i).toInt()
        if (state.item.stock != -1 && state.item.stock < amount) return
        val totalPrice = (amount * state.item.price).toInt()

        val title = when (type) {
            BuyType.BUY -> translation.buttonBuyAmount(amount)
            BuyType.SELL -> translation.buttonSellAmount(amount)
        }

        val priceDescription = when (type) {
            BuyType.BUY -> translation.shopInfoPrice(totalPrice)
            BuyType.SELL -> translation.shopInfoPrice(totalPrice)
        }

        val itemStack = state.item.toItemStack().copy(amount).withMeta {
            setDisplayName(title)
            lore = listOf(priceDescription)
        }
        button(type.startIndex + i, itemStack) {
            when (type) {
                BuyType.BUY -> viewModel.onBuyClicked(amount)
                BuyType.SELL -> viewModel.onSellClicked(amount)
            }
        }
    }

    private fun render(buyState: BuyState) {
        inventory.clear()
        when (buyState) {
            is BuyState.Loaded -> {
                clear()
                clickListener.remember(balanceButton)
                clickListener.remember(backButton)
                balanceButton.set(inventory)
                backButton.set(inventory)
                buyInfoButton.set(inventory)
                sellInfoButton.set(inventory)
                for (i in 0 until 7) {
                    setActionButton(BuyType.SELL, i, buyState)
                    setActionButton(BuyType.BUY, i, buyState)
                }
            }

            BuyState.Loading -> {}
        }

    }


}