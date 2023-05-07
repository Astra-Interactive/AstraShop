package ru.astrainteractive.astrashop.gui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.getValue
import ru.astrainteractive.astralibs.menu.menu.InventoryButton
import ru.astrainteractive.astralibs.menu.utils.ItemStackButtonBuilder
import ru.astrainteractive.astrashop.di.impl.RootModuleImpl
import ru.astrainteractive.astrashop.domain.calculator.PriceCalculator
import ru.astrainteractive.astrashop.domain.models.ShopConfig
import ru.astrainteractive.astrashop.domain.models.SpigotShopItem
import ru.astrainteractive.astrashop.domain.models.SpigotTitleItem
import ru.astrainteractive.astrashop.gui.buy.BuyState
import ru.astrainteractive.astrashop.gui.shop.ShopGUI
import ru.astrainteractive.astrashop.utils.openOnMainThread

fun button(
    index: Int,
    item: ItemStack,
    onClick: (e: InventoryClickEvent) -> Unit = {}
) = ItemStackButtonBuilder {
    this.index = index
    this.itemStack = item
    this.onClick = onClick
}

private val translation by RootModuleImpl.translation

@Suppress("FunctionNaming")
fun BackButton(onClick: (e: InventoryClickEvent) -> Unit) = ItemStackButtonBuilder {
    this.index = 49
    this.itemStack = ItemStack(Material.PAPER).apply {
        editMeta { it.setDisplayName(translation.buttonBack) }
    }
    this.onClick = onClick
}

val NextButton: InventoryButton
    get() = ItemStackButtonBuilder {
        this.index = 53
        this.itemStack = ItemStack(Material.PAPER).apply {
            editMeta { it.setDisplayName(translation.menuNextPage) }
        }
    }

val PrevButton: InventoryButton
    get() = ItemStackButtonBuilder {
        this.index = 45
        this.itemStack = ItemStack(Material.PAPER).apply {
            editMeta { it.setDisplayName(translation.menuPrevPage) }
        }
    }

val BuyInfoButton: InventoryButton
    get() = ItemStackButtonBuilder {
        this.index = 1
        this.itemStack = ItemStack(Material.GREEN_STAINED_GLASS).apply {
            editMeta {
                it.setDisplayName(translation.buttonBuy)
            }
        }
    }

val SellInfoButton: InventoryButton
    get() = ItemStackButtonBuilder {
        this.index = 10
        this.itemStack = ItemStack(Material.RED_STAINED_GLASS).apply {
            editMeta {
                it.setDisplayName(translation.buttonSell)
            }
        }
    }

@Suppress("FunctionNaming")
fun BalanceButton(state: BuyState.Loaded? = null): InventoryButton {
    return ItemStackButtonBuilder {
        this.index = 0
        this.itemStack = ItemStack(Material.EMERALD).apply {
            editMeta {
                it.setDisplayName(translation.buttonInformation)
            }
            val stock = state?.item?.stock ?: -1
            lore = listOf(
                translation.shopInfoStock(stock),
                translation.shopInfoPrice(state?.item?.let { PriceCalculator.calculateBuyPrice(it, 1) } ?: 0),
                translation.shopInfoSellPrice(state?.item?.let { PriceCalculator.calculateSellPrice(it, 1) } ?: 0),
                translation.shopInfoBalance(state?.playerBalance ?: 0)
            )
        }
    }
}

@Suppress("FunctionNaming")
fun BackToShopButton(
    shopConfig: ShopConfig<SpigotTitleItem, SpigotShopItem>,
    playerHolder: ShopPlayerHolder,
    lifecycleScope: CoroutineScope
): InventoryButton {
    return ItemStackButtonBuilder {
        this.index = 9
        this.itemStack = ItemStack(Material.BARRIER).apply {
            editMeta {
                it.setDisplayName(translation.buttonBack)
            }
        }
        this.onClick = {
            lifecycleScope.launch(Dispatchers.IO) {
                ShopGUI(shopConfig, playerHolder).openOnMainThread()
            }
        }
    }
}
