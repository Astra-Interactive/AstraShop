package ru.astrainteractive.astrashop.gui.buy.ui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.menu.menu.Menu
import ru.astrainteractive.astralibs.menu.menu.MenuSize
import ru.astrainteractive.astralibs.menu.menu.addLore
import ru.astrainteractive.astralibs.menu.menu.setDisplayName
import ru.astrainteractive.astralibs.menu.menu.setIndex
import ru.astrainteractive.astralibs.menu.menu.setItemStack
import ru.astrainteractive.astralibs.menu.menu.setMaterial
import ru.astrainteractive.astralibs.menu.menu.setOnClickListener
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.core.PluginTranslation
import ru.astrainteractive.astrashop.domain.usecase.CalculatePriceUseCase
import ru.astrainteractive.astrashop.domain.util.ItemStackExt.copy
import ru.astrainteractive.astrashop.domain.util.ItemStackExt.toItemStack
import ru.astrainteractive.astrashop.gui.buy.model.BuyType
import ru.astrainteractive.astrashop.gui.buy.presentation.BuyComponent
import ru.astrainteractive.astrashop.gui.buy.presentation.BuyComponent.Model
import ru.astrainteractive.astrashop.gui.model.ShopPlayerHolder
import ru.astrainteractive.astrashop.gui.router.GuiRouter
import kotlin.math.pow

@Suppress("LongParameterList")
class BuyGUI(
    shopConfig: ShopConfig,
    item: ShopConfig.ShopItem,
    override val playerHolder: ShopPlayerHolder,
    private val translation: PluginTranslation,
    private val buyComponent: BuyComponent,
    private val calculatePriceUseCase: CalculatePriceUseCase,
    private val router: GuiRouter,
    translationContext: BukkitTranslationContext
) : Menu(), BukkitTranslationContext by translationContext {
    override val menuSize: MenuSize = MenuSize.XS

    override var menuTitle: Component = item.toItemStack().itemMeta
        .displayName
        .ifEmpty { item.toItemStack().type.name }
        .let(StringDesc::Raw)
        .toComponent()

    private val backButton = InventorySlot.Builder()
        .setIndex(9)
        .setMaterial(Material.BARRIER)
        .setDisplayName(translation.buttons.buttonBack.toComponent())
        .setOnClickListener {
            val route = GuiRouter.Route.Shop(
                playerHolder = playerHolder,
                shopConfig = shopConfig
            )
            componentScope.launch(Dispatchers.IO) { router.open(route) }
        }.build()

    private val buyInfoButton = InventorySlot.Builder()
        .setIndex(1)
        .setMaterial(Material.GREEN_STAINED_GLASS)
        .setDisplayName(translation.buttons.buttonBuy.toComponent())
        .build()

    private val sellInfoButton = InventorySlot.Builder()
        .setIndex(10)
        .setMaterial(Material.RED_STAINED_GLASS)
        .setDisplayName(translation.buttons.buttonSell.toComponent())
        .build()

    private val balanceButton: InventorySlot
        get() {
            val state = buyComponent.model.value as? Model.Loaded
            val stockAmount = state?.item?.stock ?: -1
            val buyPrice = state?.item?.let { calculatePriceUseCase.calculateBuyPrice(it, 1) } ?: 0
            val sellPrice = state?.item?.let { calculatePriceUseCase.calculateSellPrice(it, 1) } ?: 0
            val balance = state?.playerBalance ?: 0
            return InventorySlot.Builder()
                .setIndex(0)
                .setMaterial(Material.EMERALD)
                .setDisplayName(translation.buttons.buttonInformation.toComponent())
                .addLore(translation.buttons.shopInfoStock(stockAmount).toComponent())
                .addLore(translation.buttons.shopInfoBuyPrice(buyPrice).toComponent())
                .addLore(translation.buttons.shopInfoSellPrice(sellPrice).toComponent())
                .addLore(translation.buttons.shopInfoBalance(balance).toComponent())
                .build()
        }

    override fun onCreated() {
        buyComponent.model
            .onEach { render() }
            .launchIn(componentScope)
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

        val totalPriceBuy = calculatePriceUseCase.calculateBuyPrice(state.item, amount).coerceAtLeast(0.0)
        val totalPriceSell = calculatePriceUseCase.calculateSellPrice(state.item, amount).coerceAtLeast(0.0)

        val title = when (type) {
            BuyType.BUY -> translation.buttons.buttonBuyAmount(amount)
            BuyType.SELL -> translation.buttons.buttonSellAmount(amount)
        }

        val priceDescription = when (type) {
            BuyType.BUY -> translation.buttons.shopInfoBuyPrice(totalPriceBuy)
            BuyType.SELL -> translation.buttons.shopInfoBuyPrice(totalPriceSell)
        }

        val itemStack = state.item.toItemStack().copy(amount).apply {
            editMeta {
                it.displayName(title.toComponent())
            }
            lore(listOf(priceDescription.toComponent()))
        }
        InventorySlot.Builder()
            .setIndex(type.startIndex + i)
            .setItemStack(itemStack)
            .setOnClickListener {
                when (type) {
                    BuyType.BUY -> buyComponent.onBuyClicked(amount)
                    BuyType.SELL -> buyComponent.onSellClicked(amount)
                }
            }.build().setInventorySlot()
    }

    override fun render() {
        super.render()

        when (val state = buyComponent.model.value) {
            is Model.Loaded -> {
                clickListener.remember(balanceButton)

                balanceButton.setInventorySlot()
                backButton.setInventorySlot()
                buyInfoButton.setInventorySlot()
                sellInfoButton.setInventorySlot()

                for (i in 0 until 7) {
                    setActionButton(BuyType.SELL, i, state)
                    setActionButton(BuyType.BUY, i, state)
                }
            }

            Model.Loading -> Unit

            Model.Error -> Unit
        }
    }
}
