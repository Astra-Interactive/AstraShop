package ru.astrainteractive.astrashop.gui.shop.ui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.menu.menu.MenuSize
import ru.astrainteractive.astralibs.menu.menu.PaginatedMenu
import ru.astrainteractive.astralibs.menu.menu.addLore
import ru.astrainteractive.astralibs.menu.menu.setDisplayName
import ru.astrainteractive.astralibs.menu.menu.setIndex
import ru.astrainteractive.astralibs.menu.menu.setItemStack
import ru.astrainteractive.astralibs.menu.menu.setLore
import ru.astrainteractive.astralibs.menu.menu.setMaterial
import ru.astrainteractive.astralibs.menu.menu.setOnClickListener
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.core.PluginPermission
import ru.astrainteractive.astrashop.core.PluginTranslation
import ru.astrainteractive.astrashop.domain.usecase.CalculatePriceUseCase
import ru.astrainteractive.astrashop.domain.util.ItemStackExt.toItemStack
import ru.astrainteractive.astrashop.gui.model.ShopPlayerHolder
import ru.astrainteractive.astrashop.gui.router.GuiRouter
import ru.astrainteractive.astrashop.gui.shop.presentation.ShopComponent
import ru.astrainteractive.astrashop.gui.shop.presentation.ShopComponent.Intent
import ru.astrainteractive.astrashop.gui.shop.presentation.ShopComponent.Model
import ru.astrainteractive.astrashop.gui.shop.util.PagingProvider
import ru.astrainteractive.astrashop.gui.util.Buttons

@Suppress("LongParameterList")
class ShopGUI(
    private val shopConfig: ShopConfig,
    override val playerHolder: ShopPlayerHolder,
    private val translation: PluginTranslation,
    private val calculatePriceUseCase: CalculatePriceUseCase,
    private val router: GuiRouter,
    shopComponentFactory: (PagingProvider) -> ShopComponent,
    translationContext: BukkitTranslationContext
) : PaginatedMenu(), PagingProvider, BukkitTranslationContext by translationContext {
    private val shopComponent = shopComponentFactory.invoke(this)
    private val buttons = Buttons(
        translation = translation,
        translationContext = translationContext,
        menu = this
    )

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
        val cleanPlayerHolder = ShopPlayerHolder(playerHolder.player)
        val route = GuiRouter.Route.Shops(cleanPlayerHolder)
        componentScope.launch(Dispatchers.IO) { router.open(route) }
    }

    override fun onInventoryClicked(e: InventoryClickEvent) {
        super.onInventoryClicked(e)
        e.isCancelled = true

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
        shopComponent.model
            .onEach { render() }
            .launchIn(componentScope)
    }

    private fun renderEditModeButton() {
        if (!playerHolder.player.toPermissible().hasPermission(PluginPermission.EditShop)) return
        val material = when (shopComponent.model.value) {
            is Model.Loading, is Model.List -> Material.LIGHT
            is Model.ListEditMode -> Material.BARRIER
        }
        val displayName = when (shopComponent.model.value) {
            is Model.Loading, is Model.List -> translation.buttons.buttonEditModeDisabled.toComponent()
            is Model.ListEditMode -> translation.buttons.buttonEditModeEnabled.toComponent()
        }
        val lore = when (shopComponent.model.value) {
            is Model.Loading, is Model.List -> listOf(translation.buttons.buttonEditModeEnter.toComponent())
            is Model.ListEditMode -> listOf(translation.buttons.buttonEditModeExit.toComponent())
        }
        InventorySlot.Builder()
            .setIndex(prevPageButton.index + 1)
            .setMaterial(material)
            .setDisplayName(displayName)
            .setLore(lore)
            .setOnClickListener {
                if (playerHolder.player.toPermissible().hasPermission(PluginPermission.EditShop)) {
                    shopComponent.onIntent(Intent.ToggleEditModeClick)
                }
            }.build().setInventorySlot()
    }

    private fun renderItemList(items: Map<String, ShopConfig.ShopItem>) {
        for (i in 0 until maxItemsPerPage) {
            val index = maxItemsPerPage * page + i
            val item = items[index.toString()] ?: continue
            val buyPrice = calculatePriceUseCase.calculateBuyPrice(item, 1)
            val sellPrice = calculatePriceUseCase.calculateSellPrice(item, 1)
            val editModeHint = when (shopComponent.model.value) {
                !is Model.ListEditMode -> translation.menu.menuEdit.toComponent()
                else -> StringDesc.Raw("").toComponent()
            }
            InventorySlot.Builder()
                .setIndex(i)
                .setItemStack(item.toItemStack())
                .addLore(translation.buttons.shopInfoStock(item.stock).toComponent())
                .addLore(translation.buttons.shopInfoBuyPrice(buyPrice).toComponent())
                .addLore(translation.buttons.shopInfoSellPrice(sellPrice).toComponent())
                .addLore(translation.menu.menuDeleteItem.toComponent())
                .addLore(editModeHint)
                .setOnClickListener {
                    val isValid = it.isLeftClick && !it.isShiftClick && shopComponent.model.value is Model.List
                    if (!isValid) return@setOnClickListener
                    val route = GuiRouter.Route.Buy(
                        playerHolder = playerHolder,
                        shopConfig = shopConfig,
                        shopItem = item
                    )
                    componentScope.launch(Dispatchers.IO) { router.open(route) }
                }.build().setInventorySlot()
        }
    }

    override fun render() {
        super.render()
        when (val state = shopComponent.model.value) {
            is Model.ListEditMode -> {
                renderEditModeButton()
                renderItemList(state.items)
            }

            is Model.List -> {
                renderEditModeButton()
                renderItemList(state.items)
            }

            Model.Loading -> Unit
        }
    }
}
