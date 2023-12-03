package ru.astrainteractive.astrashop.gui.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.menu.menu.Menu
import ru.astrainteractive.astralibs.menu.menu.PaginatedMenu
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astrashop.core.PluginTranslation
import ru.astrainteractive.astrashop.domain.usecase.CalculatePriceUseCase
import ru.astrainteractive.astrashop.gui.buy.presentation.BuyComponent

class Buttons(
    private val lifecycleScope: CoroutineScope,
    private val translation: PluginTranslation,
    private val translationContext: BukkitTranslationContext,
    private val menu: Menu
) : BukkitTranslationContext by translationContext {

    fun button(
        index: Int,
        item: ItemStack,
        onClick: Click = Click { }
    ) = InventorySlot.Builder {
        this.index = index
        this.itemStack = item
        this.click = onClick
    }

    @Suppress("FunctionNaming")
    fun backButton(
        onClick: Click,
    ) = InventorySlot.Builder {
        this.index = 49
        this.itemStack = ItemStack(Material.PAPER).apply {
            editMeta { it.displayName(translation.buttons.buttonBack.toComponent()) }
        }
        this.click = onClick
    }

    val nextButton: InventorySlot
        get() = InventorySlot.Builder {
            this.index = 53
            this.itemStack = ItemStack(Material.PAPER).apply {
                editMeta { it.displayName(translation.menu.menuNextPage.toComponent()) }
            }
            this.click = Click {
                val menu = menu as PaginatedMenu
                menu.showPage(menu.page + 1)
            }
        }

    val prevButton: InventorySlot
        get() = InventorySlot.Builder {
            this.index = 45
            this.itemStack = ItemStack(Material.PAPER).apply {
                editMeta { it.displayName(translation.menu.menuPrevPage.toComponent()) }
            }
            this.click = Click {
                val menu = menu as PaginatedMenu
                menu.showPage(menu.page - 1)
            }
        }

    val buyInfoButton: InventorySlot
        get() = InventorySlot.Builder {
            this.index = 1
            this.itemStack = ItemStack(Material.GREEN_STAINED_GLASS).apply {
                editMeta {
                    it.displayName(translation.buttons.buttonBuy.toComponent())
                }
            }
        }

    val sellInfoButton: InventorySlot
        get() = InventorySlot.Builder {
            this.index = 10
            this.itemStack = ItemStack(Material.RED_STAINED_GLASS).apply {
                editMeta {
                    it.displayName(translation.buttons.buttonSell.toComponent())
                }
            }
        }

    @Suppress("FunctionNaming")
    fun balanceButton(
        state: BuyComponent.Model.Loaded? = null,
        calculatePriceUseCase: CalculatePriceUseCase
    ): InventorySlot {
        return InventorySlot.Builder {
            this.index = 0
            this.itemStack = ItemStack(Material.EMERALD).apply {
                editMeta {
                    it.displayName(translation.buttons.buttonInformation.toComponent())
                }
                val stock = state?.item?.stock ?: -1
                lore(
                    listOf(
                        translation.buttons.shopInfoStock(stock).toComponent(),
                        translation.buttons.shopInfoPrice(
                            state?.item?.let { calculatePriceUseCase.calculateBuyPrice(it, 1) }
                                ?: 0
                        ).toComponent(),
                        translation.buttons.shopInfoSellPrice(
                            state?.item?.let {
                                calculatePriceUseCase.calculateSellPrice(
                                    it,
                                    1
                                )
                            }
                                ?: 0
                        ).toComponent(),
                        translation.buttons.shopInfoBalance(state?.playerBalance ?: 0).toComponent()
                    )
                )
            }
        }
    }

    @Suppress("FunctionNaming")
    fun backToShopButton(): InventorySlot {
        return InventorySlot.Builder {
            this.index = 9
            this.itemStack = ItemStack(Material.BARRIER).apply {
                editMeta {
                    it.displayName(translation.buttons.buttonBack.toComponent())
                }
            }
            this.click = Click {
                lifecycleScope.launch(Dispatchers.IO) {
                    TODO()
//                    ShopGUI(shopConfig, playerHolder).openOnMainThread()
                }
            }
        }
    }
}
