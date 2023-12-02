package ru.astrainteractive.astrashop.gui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.menu.menu.PaginatedMenu
import ru.astrainteractive.astrashop.di.impl.RootModuleImpl
import ru.astrainteractive.astrashop.domain.calculator.PriceCalculator
import ru.astrainteractive.astrashop.domain.model.ShopConfig
import ru.astrainteractive.astrashop.domain.model.SpigotShopItemStack
import ru.astrainteractive.astrashop.domain.model.SpigotTitleItemStack
import ru.astrainteractive.astrashop.gui.buy.BuyState
import ru.astrainteractive.astrashop.gui.shop.ShopGUI
import ru.astrainteractive.astrashop.util.openOnMainThread
import ru.astrainteractive.klibs.kdi.getValue

fun button(
    index: Int,
    item: ItemStack,
    onClick: Click = Click { }
) = InventorySlot.Builder {
    this.index = index
    this.itemStack = item
    this.click = onClick
}

private val translation by RootModuleImpl.translation

@Suppress("FunctionNaming")
fun BackButton(onClick: Click) = InventorySlot.Builder {
    this.index = 49
    this.itemStack = ItemStack(Material.PAPER).apply {
        editMeta { it.setDisplayName(translation.buttonBack) }
    }
    this.click = onClick
}

val PaginatedMenu.NextButton: InventorySlot
    get() = InventorySlot.Builder {
        this.index = 53
        this.itemStack = ItemStack(Material.PAPER).apply {
            editMeta { it.setDisplayName(translation.menuNextPage) }
        }
        this.click = Click { showPage(page + 1) }
    }

val PaginatedMenu.PrevButton: InventorySlot
    get() = InventorySlot.Builder {
        this.index = 45
        this.itemStack = ItemStack(Material.PAPER).apply {
            editMeta { it.setDisplayName(translation.menuPrevPage) }
        }
        this.click = Click { showPage(page - 1) }
    }

val BuyInfoButton: InventorySlot
    get() = InventorySlot.Builder {
        this.index = 1
        this.itemStack = ItemStack(Material.GREEN_STAINED_GLASS).apply {
            editMeta {
                it.setDisplayName(translation.buttonBuy)
            }
        }
    }

val SellInfoButton: InventorySlot
    get() = InventorySlot.Builder {
        this.index = 10
        this.itemStack = ItemStack(Material.RED_STAINED_GLASS).apply {
            editMeta {
                it.setDisplayName(translation.buttonSell)
            }
        }
    }

@Suppress("FunctionNaming")
fun BalanceButton(state: BuyState.Loaded? = null): InventorySlot {
    return InventorySlot.Builder {
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
    shopConfig: ShopConfig,
    playerHolder: ShopPlayerHolder,
    lifecycleScope: CoroutineScope
): InventorySlot {
    return InventorySlot.Builder {
        this.index = 9
        this.itemStack = ItemStack(Material.BARRIER).apply {
            editMeta {
                it.setDisplayName(translation.buttonBack)
            }
        }
        this.click = Click {
            lifecycleScope.launch(Dispatchers.IO) {
                ShopGUI(shopConfig, playerHolder).openOnMainThread()
            }
        }
    }
}
