package ru.astrainteractive.astrashop.gui.shop.ui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.menu.clicker.MenuClickListener
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.menu.menu.MenuSize
import ru.astrainteractive.astralibs.menu.menu.PaginatedMenu
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astrashop.api.calculator.PriceCalculator
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.core.PluginPermission
import ru.astrainteractive.astrashop.core.PluginTranslation
import ru.astrainteractive.astrashop.gui.model.ShopPlayerHolder
import ru.astrainteractive.astrashop.gui.shop.presentation.ShopComponent
import ru.astrainteractive.astrashop.gui.shop.presentation.ShopComponent.Intent
import ru.astrainteractive.astrashop.gui.shop.presentation.ShopComponent.Model
import ru.astrainteractive.astrashop.gui.shop.util.PagingProvider
import ru.astrainteractive.astrashop.gui.util.Buttons
import ru.astrainteractive.astrashop.util.toItemStack

class ShopGUI(
    private val shopConfig: ShopConfig,
    override val playerHolder: ShopPlayerHolder,
    private val translation: PluginTranslation,
    private val shopComponentFactory: (PagingProvider) -> ShopComponent,
    translationContext: BukkitTranslationContext
) : PaginatedMenu(), PagingProvider, BukkitTranslationContext by translationContext {
    private val shopComponent = shopComponentFactory.invoke(this)
    private val buttons = Buttons(
        lifecycleScope = this,
        translation = translation,
        translationContext = translationContext,
        menu = this
    )
    private val clickListener = MenuClickListener()

    override val menuSize: MenuSize = MenuSize.XL
    override var menuTitle: Component = shopConfig.options.title.toComponent()
    override var page: Int
        get() = playerHolder.shopPage
        set(value) {
            playerHolder.shopPage = value
        }
    override val maxItemsAmount: Int
        get() = shopComponent.maxItemsAmount

    override val nextPageButton: InventorySlot = buttons.nextButton
    override val prevPageButton: InventorySlot = buttons.prevButton
    override val backPageButton: InventorySlot = buttons.backButton {
        shopComponent.onIntent(Intent.OpenShops(playerHolder))
    }

    override fun onInventoryClicked(e: InventoryClickEvent) {
        e.isCancelled = true
        clickListener.onClick(e)

        if (playerHolder.player.toPermissible().hasPermission(PluginPermission.EditShop)) {
            shopComponent.onIntent(Intent.DeleteItem(e, e.isRightClick, e.isShiftClick))
        }
        if (!listOf(
                prevPageButton.index + 1,
                backPageButton.index,
                prevPageButton.index,
                nextPageButton.index
            ).contains(e.slot)
        ) {
            Intent.InventoryClick(e).also(shopComponent::onIntent)
        }
    }

    override fun onInventoryClose(it: InventoryCloseEvent) {
        shopComponent.cancel()
        close()
    }

    override fun onPageChanged() {
        render()
    }

    override fun onCreated() {
        shopComponent.model.collectOn(Dispatchers.IO, block = ::render)
    }

    private fun renderEditModeButton() {
        if (!playerHolder.player.toPermissible().hasPermission(PluginPermission.EditShop)) return
        val itemStack = when (shopComponent.model.value) {
            is Model.Loading, is Model.List -> ItemStack(Material.LIGHT).apply {
                editMeta {
                    it.displayName(translation.buttons.buttonEditModeDisabled.toComponent())
                }
                lore(listOf(translation.buttons.buttonEditModeEnter.toComponent()))
            }

            is Model.ListEditMode -> ItemStack(Material.BARRIER).apply {
                editMeta {
                    it.displayName(translation.buttons.buttonEditModeEnabled.toComponent())
                }
                lore(listOf(translation.buttons.buttonEditModeExit.toComponent()))
            }
        }

        buttons.button(prevPageButton.index + 1, itemStack) {
            if (playerHolder.player.toPermissible().hasPermission(PluginPermission.EditShop)) {
                shopComponent.onIntent(Intent.ToggleEditModeClick)
            }
        }.also(clickListener::remember).setInventorySlot()
    }

    private fun renderItemList(items: Map<String, ShopConfig.ShopItem>) {
        for (i in 0 until maxItemsPerPage) {
            val index = maxItemsPerPage * page + i
            val item = items[index.toString()] ?: continue
            val itemStack = item.toItemStack().apply {
                lore(
                    listOf(
                        translation.buttons.shopInfoStock(item.stock).toComponent(),
                        translation.buttons.shopInfoPrice(PriceCalculator.calculateBuyPrice(item, 1)).toComponent(),
                        translation.buttons.shopInfoSellPrice(PriceCalculator.calculateSellPrice(item, 1))
                            .toComponent(),
                        translation.menu.menuDeleteItem.toComponent(),
                        if (shopComponent.model.value !is Model.ListEditMode) {
                            translation.menu.menuEdit.toComponent()
                        } else {
                            StringDesc.Raw("").toComponent()
                        },
                    )
                )
            }
            buttons.button(i, itemStack) {
                Intent.OpenBuyGui(
                    shopConfig,
                    item,
                    playerHolder,
                    it.isLeftClick,
                    it.isShiftClick,
                    shopComponent.model.value
                ).also(shopComponent::onIntent)
            }.also(clickListener::remember).setInventorySlot()
        }
    }

    private fun render(state: Model = shopComponent.model.value) {
        inventory.clear()
        clickListener.clearClickListener()
        setManageButtons(clickListener)
        clickListener.remember(backPageButton)
        when (state) {
            is Model.ListEditMode -> {
                renderEditModeButton()
                renderItemList(state.items)
            }

            is Model.List -> {
                renderEditModeButton()
                renderItemList(state.items)
            }

            Model.Loading -> {}
        }
    }
}
