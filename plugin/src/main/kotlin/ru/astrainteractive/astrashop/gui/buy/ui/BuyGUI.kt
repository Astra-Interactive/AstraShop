package ru.astrainteractive.astrashop.gui.buy.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.jetbrains.kotlin.konan.util.visibleName
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.menu.menu.Menu
import ru.astrainteractive.astralibs.menu.menu.MenuSize
import ru.astrainteractive.astralibs.menu.menu.addLore
import ru.astrainteractive.astralibs.menu.menu.setDisplayName
import ru.astrainteractive.astralibs.menu.menu.setIndex
import ru.astrainteractive.astralibs.menu.menu.setItemStack
import ru.astrainteractive.astralibs.menu.menu.setMaterial
import ru.astrainteractive.astralibs.menu.menu.setOnClickListener
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.core.PluginTranslation
import ru.astrainteractive.astrashop.domain.usecase.PriceCalculator
import ru.astrainteractive.astrashop.domain.util.ItemStackExt.copy
import ru.astrainteractive.astrashop.domain.util.ItemStackExt.toItemStack
import ru.astrainteractive.astrashop.gui.buy.model.BuyType
import ru.astrainteractive.astrashop.gui.buy.presentation.BuyComponent
import ru.astrainteractive.astrashop.gui.buy.presentation.BuyComponent.Model
import ru.astrainteractive.astrashop.gui.model.ShopPlayerHolder
import ru.astrainteractive.astrashop.gui.router.GuiRouter
import ru.astrainteractive.astrashop.util.RoundExt.round
import kotlin.math.pow

@Suppress("LongParameterList")
class BuyGUI(
    shopConfig: ShopConfig,
    item: ShopConfig.ShopItem,
    override val playerHolder: ShopPlayerHolder,
    private val translation: PluginTranslation,
    private val buyComponent: BuyComponent,
    private val router: GuiRouter,
    kyoriComponentSerializer: KyoriComponentSerializer
) : Menu(), KyoriComponentSerializer by kyoriComponentSerializer {
    override val menuSize: MenuSize = MenuSize.XS
    override val childComponents: List<CoroutineScope> = listOf(buyComponent)

    override var menuTitle: Component = item.toItemStack()
        .itemMeta
        .displayName()
        ?: item.toItemStack().type.visibleName
            .let(StringDesc::Raw)
            .let(::toComponent)

    private val backButton = InventorySlot.Builder()
        .setIndex(9)
        .setMaterial(Material.BARRIER)
        .setDisplayName(translation.buttons.buttonBack.let(::toComponent))
        .setOnClickListener {
            val route = GuiRouter.Route.Shop(
                playerHolder = playerHolder,
                shopConfig = shopConfig
            )
            menuScope.launch(Dispatchers.IO) { router.open(route) }
        }.build()

    private val buyInfoButton = InventorySlot.Builder()
        .setIndex(1)
        .setMaterial(Material.GREEN_STAINED_GLASS)
        .setDisplayName(translation.buttons.buttonBuy.let(::toComponent))
        .build()

    private val sellInfoButton = InventorySlot.Builder()
        .setIndex(10)
        .setMaterial(Material.RED_STAINED_GLASS)
        .setDisplayName(translation.buttons.buttonSell.let(::toComponent))
        .build()

    private val balanceButton: InventorySlot
        get() {
            val state = buyComponent.model.value as? Model.Loaded
            val stockAmount = state?.item?.stock ?: -1
            val buyPrice = state?.item?.let { PriceCalculator.calculateBuyPrice(it, 1) }?.round(2) ?: 0
            val sellPrice = state?.item?.let { PriceCalculator.calculateSellPrice(it, 1) }?.round(2) ?: 0
            val balance = state?.playerBalance ?: 0
            return InventorySlot.Builder()
                .setIndex(0)
                .setMaterial(Material.EMERALD)
                .setDisplayName(translation.buttons.buttonInformation.let(::toComponent))
                .addLore(translation.buttons.shopInfoStock(stockAmount).let(::toComponent))
                .addLore(translation.buttons.shopInfoBuyPrice(buyPrice).let(::toComponent))
                .addLore(translation.buttons.shopInfoSellPrice(sellPrice).let(::toComponent))
                .addLore(translation.buttons.shopInfoBalance(balance).let(::toComponent))
                .apply {
                    if (state?.item?.isPurchaseInfinite == true) {
                        addLore(translation.shop.infinitePurchase.let(::toComponent))
                    }
                }
                .build()
        }

    override fun onCreated() {
        buyComponent.model
            .onEach { render() }
            .launchIn(menuScope)
    }

    override fun onInventoryClicked(e: InventoryClickEvent) {
        super.onInventoryClicked(e)
        e.isCancelled = true
    }

    @Suppress("ComplexCondition")
    private fun setActionButton(type: BuyType, i: Int, state: Model.Loaded) {
        val amount = 2.0.pow(i).toInt()
        if (type == BuyType.BUY &&
            state.item.stock != -1 &&
            state.item.stock < amount &&
            !state.item.isPurchaseInfinite
        ) {
            return
        }

        val totalPriceBuy = PriceCalculator.calculateBuyPrice(state.item, amount).coerceAtLeast(0.0).round(2)
        val totalPriceSell = PriceCalculator.calculateSellPrice(state.item, amount).coerceAtLeast(0.0).round(2)

        val title = when (type) {
            BuyType.BUY -> translation.buttons.buttonBuyAmount(amount)
            BuyType.SELL -> translation.buttons.buttonSellAmount(amount)
        }

        val priceDescription = when (type) {
            BuyType.BUY -> translation.buttons.shopInfoBuyPrice(totalPriceBuy)
            BuyType.SELL -> translation.buttons.shopInfoSellPrice(totalPriceSell)
        }

        val itemStack = state.item.toItemStack().copy(amount).apply {
            editMeta {
                it.displayName(title.let(::toComponent))
            }
            lore(listOf(priceDescription.let(::toComponent)))
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
