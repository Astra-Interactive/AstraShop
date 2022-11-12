package ru.astrainteractive.astrashop.gui.shop

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.architecture.ViewModel
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astrashop.domain.models.ShopItemStack
import ru.astrainteractive.astrashop.domain.models.ShopMaterial
import ru.astrainteractive.astrashop.modules.DataSourceModule

interface PagingProvider {
    val page: Int
    val maxItemsPerPage: Int
    fun index(i: Int) = i + maxItemsPerPage * page
}

class ShopViewModel(private val configName: String, private val pagingProvider: PagingProvider) : ViewModel() {
    val state = MutableStateFlow<ShopListState>(ShopListState.Loading)
    private val dataSource by DataSourceModule

    private fun onEditStateClicked(state: ShopListState.ListEditMode, e: InventoryClickEvent) {
        if (e.isShiftClick && e.isRightClick && e.clickedInventory?.holder is ShopGUI) {

            state.config.items.remove(pagingProvider.index(e.slot).toString())
            val config = state.config
            viewModelScope.launch(Dispatchers.IO) {
                dataSource.updateShop(config)
                load()
            }
        }
        if (e.clickedInventory?.holder is ShopGUI) return
        val clickedItem = e.currentItem ?: return

        val index = pagingProvider.index(state.clickedSlot)

        state.config.items[index.toString()] =
            if (clickedItem.isSimple()) ShopMaterial(
                itemIndex = index,
                material = clickedItem.type
            ) else ShopItemStack(itemIndex = index, itemStack = clickedItem)
        val config = state.config
        viewModelScope.launch(Dispatchers.IO) {
            dataSource.updateShop(config)
            load()
        }
    }

    fun ItemStack.isSimple(): Boolean {
        print("""
            hasItemMeta: ${hasItemMeta()}\n
            hasDisplayName: ${itemMeta.hasDisplayName()}\n
            hasEnchants: ${itemMeta.hasEnchants()}\n
            hasAttributeModifiers: ${itemMeta.hasAttributeModifiers()}\n
            hasCustomModelData: ${itemMeta.hasCustomModelData()}\n
            hasLore: ${itemMeta.hasLore()}\n
        """.trimIndent())
        return !hasItemMeta() ||
                (!itemMeta.hasDisplayName()
                        && !itemMeta.hasEnchants()
                        && !itemMeta.hasAttributeModifiers()
                        && !itemMeta.hasCustomModelData()
                        && !itemMeta.hasLore())
    }

    fun onClicked(e: InventoryClickEvent) {
        when (val state = state.value) {
            is ShopListState.ListEditMode -> onEditStateClicked(state, e)
            is ShopListState.List -> {
                if (!e.isRightClick) return
                if (e.clickedInventory?.holder !is ShopGUI) return
                this.state.value = ShopListState.ListEditMode(state.config, e.slot)
            }

            ShopListState.Loading -> Unit
        }
    }

    fun load() = viewModelScope.launch(Dispatchers.IO) {
        state.value = ShopListState.Loading
        val config = dataSource.fetchShop(configName)
        state.value = ShopListState.List(config)
    }

    fun exitEditMode() {
        val state = state.value as? ShopListState.ListEditMode?:return
        this.state.value = ShopListState.List(state.config)
    }

    init {
        load()
    }
}