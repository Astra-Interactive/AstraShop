package ru.astrainteractive.astrashop.gui.buy.ui

import kotlinx.coroutines.Dispatchers
import net.kyori.adventure.text.Component
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import ru.astrainteractive.astralibs.menu.clicker.MenuClickListener
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.menu.menu.Menu
import ru.astrainteractive.astralibs.menu.menu.MenuSize
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astrashop.api.calculator.PriceCalculator
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.core.PluginTranslation
import ru.astrainteractive.astrashop.gui.buy.model.BuyType
import ru.astrainteractive.astrashop.gui.buy.presentation.BuyComponent
import ru.astrainteractive.astrashop.gui.buy.presentation.BuyComponent.Model
import ru.astrainteractive.astrashop.gui.model.ShopPlayerHolder
import ru.astrainteractive.astrashop.gui.util.Buttons
import ru.astrainteractive.astrashop.util.copy
import ru.astrainteractive.astrashop.util.toItemStack
import kotlin.math.pow

class BuyGUI(
    item: ShopConfig.ShopItem,
    override val playerHolder: ShopPlayerHolder,
    private val translation: PluginTranslation,
    private val buyComponent: BuyComponent,
    translationContext: BukkitTranslationContext
) : Menu(), BukkitTranslationContext by translationContext {
    private val buttons = Buttons(
        lifecycleScope = this,
        translation = translation,
        translationContext = translationContext,
        menu = this
    )
    private val clickListener = MenuClickListener()

    override val menuSize: MenuSize = MenuSize.XS
    override var menuTitle: Component = item.toItemStack().itemMeta
        .displayName
        .ifEmpty { item.toItemStack().type.name }
        .let(StringDesc::Raw)
        .toComponent()

    private val backButton = buttons.backToShopButton()
    private val buyInfoButton = buttons.buyInfoButton
    private val sellInfoButton = buttons.sellInfoButton
    private val balanceButton: InventorySlot
        get() = buttons.balanceButton(buyComponent.model.value as? Model.Loaded)

    override fun onCreated() {
        buyComponent.model.collectOn(Dispatchers.IO, block = ::render)
    }

    override fun onInventoryClicked(e: InventoryClickEvent) {
        e.isCancelled = true
        clickListener.onClick(e)
    }

    override fun onInventoryClose(it: InventoryCloseEvent) {
        close()
    }

    private fun setActionButton(type: BuyType, i: Int, state: Model.Loaded) {
        val amount = 2.0.pow(i).toInt()
        if (type == BuyType.BUY && state.item.stock != -1 && state.item.stock < amount) return

        val totalPriceBuy = PriceCalculator.calculateBuyPrice(state.item, amount).coerceAtLeast(0.0)
        val totalPriceSell = PriceCalculator.calculateSellPrice(state.item, amount).coerceAtLeast(0.0)

        val title = when (type) {
            BuyType.BUY -> translation.buttons.buttonBuyAmount(amount)
            BuyType.SELL -> translation.buttons.buttonSellAmount(amount)
        }

        val priceDescription = when (type) {
            BuyType.BUY -> translation.buttons.shopInfoPrice(totalPriceBuy)
            BuyType.SELL -> translation.buttons.shopInfoPrice(totalPriceSell)
        }

        val itemStack = state.item.toItemStack().copy(amount).apply {
            editMeta {
                it.displayName(title.toComponent())
            }
            lore(listOf(priceDescription.toComponent()))
        }
        buttons.button(type.startIndex + i, itemStack) {
            when (type) {
                BuyType.BUY -> buyComponent.onBuyClicked(amount)
                BuyType.SELL -> buyComponent.onSellClicked(amount)
            }
        }.also(clickListener::remember).setInventorySlot()
    }

    private fun render(buyState: Model) {
        inventory.clear()
        clickListener.clearClickListener()

        when (buyState) {
            is Model.Loaded -> {
                clickListener.remember(balanceButton)
                clickListener.remember(backButton)

                balanceButton.setInventorySlot()
                backButton.setInventorySlot()
                buyInfoButton.setInventorySlot()
                sellInfoButton.setInventorySlot()

                for (i in 0 until 7) {
                    setActionButton(BuyType.SELL, i, buyState)
                    setActionButton(BuyType.BUY, i, buyState)
                }
            }

            Model.Loading -> {}
        }
    }
}
