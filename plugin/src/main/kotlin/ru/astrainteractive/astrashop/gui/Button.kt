package ru.astrainteractive.astrashop.gui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.menu.IInventoryButton
import ru.astrainteractive.astrashop.domain.models.ShopConfig
import ru.astrainteractive.astrashop.gui.buy.BuyState
import ru.astrainteractive.astrashop.gui.shop.ShopGUI
import ru.astrainteractive.astrashop.modules.TranslationModule
import ru.astrainteractive.astrashop.utils.withMeta

fun button(
    index: Int,
    item: ItemStack,
    onClick: (e: InventoryClickEvent) -> Unit = {}
) = object : IInventoryButton {
    override val index: Int = index
    override val item: ItemStack = item
    override val onClick: (e: InventoryClickEvent) -> Unit = onClick
}

private val translation by TranslationModule

fun BackButton(onClick: (e: InventoryClickEvent) -> Unit): IInventoryButton = button(
    49,
    ItemStack(Material.PAPER).apply {
        editMeta { it.setDisplayName(translation.buttonBack) }
    },
    onClick
)

val NextButton: IInventoryButton
    get() = button(
        53,
        ItemStack(Material.PAPER).apply {
            editMeta { it.setDisplayName(translation.menuNextPage) }
        }
    )

val PrevButton: IInventoryButton
    get() = button(
        45,
        ItemStack(Material.PAPER).apply {
            editMeta { it.setDisplayName(translation.menuPrevPage) }
        }
    )

val BuyInfoButton: IInventoryButton
    get() = button(1, ItemStack(Material.GREEN_STAINED_GLASS).withMeta {
        setDisplayName(translation.buttonBuy)
    })

val SellInfoButton: IInventoryButton
    get() = button(10, ItemStack(Material.RED_STAINED_GLASS).withMeta {
        setDisplayName(translation.buttonSell)
    })

fun BalanceButton(state: BuyState.Loaded? = null): IInventoryButton {
    return button(0, ItemStack(Material.EMERALD).withMeta {
        setDisplayName(translation.buttonInformation)
        lore = listOf(
            translation.shopInfoStock(state?.item?.stock?:0),
            translation.shopInfoPrice(state?.item?.price?:0),
            translation.shopInfoBalance(state?.playerBalance?:0)
        )
    })
}

fun BackToShopButton(shopConfig: ShopConfig, player: Player, lifecycleScope: CoroutineScope): IInventoryButton {
    return button(9, ItemStack(Material.BARRIER).withMeta {
        setDisplayName(translation.buttonBack)
    }) {
        lifecycleScope.launch(Dispatchers.IO) {
            ShopGUI(shopConfig, player).open()
        }
    }
}