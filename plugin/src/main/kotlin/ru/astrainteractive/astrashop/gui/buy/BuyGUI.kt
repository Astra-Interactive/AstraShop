package ru.astrainteractive.astrashop.gui.buy

import kotlinx.coroutines.Dispatchers
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import ru.astrainteractive.astralibs.menu.clicker.MenuClickListener
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.menu.menu.Menu
import ru.astrainteractive.astralibs.menu.menu.MenuSize
import ru.astrainteractive.astrashop.api.calculator.PriceCalculator
import ru.astrainteractive.astrashop.api.util.SpigotShopConfigAlias
import ru.astrainteractive.astrashop.api.util.SpigotShopItemAlias
import ru.astrainteractive.astrashop.asState
import ru.astrainteractive.astrashop.di.impl.RootModuleImpl
import ru.astrainteractive.astrashop.gui.util.BackToShopButton
import ru.astrainteractive.astrashop.gui.util.BalanceButton
import ru.astrainteractive.astrashop.gui.util.BuyInfoButton
import ru.astrainteractive.astrashop.gui.util.SellInfoButton
import ru.astrainteractive.astrashop.gui.util.ShopPlayerHolder
import ru.astrainteractive.astrashop.gui.util.button
import ru.astrainteractive.astrashop.util.copy
import ru.astrainteractive.astrashop.util.toItemStack
import ru.astrainteractive.klibs.kdi.getValue
import kotlin.math.pow

class BuyGUI(
    shopConfig: SpigotShopConfigAlias,
    item: SpigotShopItemAlias,
    override val playerHolder: ShopPlayerHolder
) : Menu() {

    private val viewModel = BuyViewModel(shopConfig.configName, item.itemIndex, playerHolder.player)
    private val translation by RootModuleImpl.translation
    private val router by RootModuleImpl.router

    private val clickListener = MenuClickListener()

    override val menuSize: MenuSize = MenuSize.XS
    override var menuTitle: String = item.toItemStack().itemMeta.displayName.ifEmpty { item.toItemStack().type.name }

    private val backButton = BackToShopButton(shopConfig, playerHolder, componentScope, router)
    private val buyInfoButton = BuyInfoButton
    private val sellInfoButton = SellInfoButton
    private val balanceButton: InventorySlot
        get() = BalanceButton(viewModel.state.value.asState<BuyState.Loaded>())

    override fun onCreated() {
        viewModel.state.collectOn(Dispatchers.IO, block = ::render)
    }

    override fun onInventoryClicked(e: InventoryClickEvent) {
        e.isCancelled = true
        clickListener.onClick(e)
    }

    override fun onInventoryClose(it: InventoryCloseEvent) {
        viewModel.close()
    }

    private fun setActionButton(type: BuyType, i: Int, state: BuyState.Loaded) {
        val amount = 2.0.pow(i).toInt()
        if (type == BuyType.BUY && state.item.stock != -1 && state.item.stock < amount) return

        val totalPriceBuy = PriceCalculator.calculateBuyPrice(state.item, amount).coerceAtLeast(0.0)
        val totalPriceSell = PriceCalculator.calculateSellPrice(state.item, amount).coerceAtLeast(0.0)

        val title = when (type) {
            BuyType.BUY -> translation.buttonBuyAmount(amount)
            BuyType.SELL -> translation.buttonSellAmount(amount)
        }

        val priceDescription = when (type) {
            BuyType.BUY -> translation.shopInfoPrice(totalPriceBuy)
            BuyType.SELL -> translation.shopInfoPrice(totalPriceSell)
        }

        val itemStack = state.item.toItemStack().copy(amount).apply {
            editMeta {
                it.setDisplayName(title)
            }
            lore = listOf(priceDescription)
        }
        button(type.startIndex + i, itemStack) {
            when (type) {
                BuyType.BUY -> viewModel.onBuyClicked(amount)
                BuyType.SELL -> viewModel.onSellClicked(amount)
            }
        }.also(clickListener::remember).setInventoryButton()
    }

    private fun render(buyState: BuyState) {
        inventory.clear()
        clickListener.clearClickListener()

        when (buyState) {
            is BuyState.Loaded -> {
                clickListener.remember(balanceButton)
                clickListener.remember(backButton)

                balanceButton.setInventoryButton()
                backButton.setInventoryButton()
                buyInfoButton.setInventoryButton()
                sellInfoButton.setInventoryButton()

                for (i in 0 until 7) {
                    setActionButton(BuyType.SELL, i, buyState)
                    setActionButton(BuyType.BUY, i, buyState)
                }
            }

            BuyState.Loading -> {}
        }
    }
}
