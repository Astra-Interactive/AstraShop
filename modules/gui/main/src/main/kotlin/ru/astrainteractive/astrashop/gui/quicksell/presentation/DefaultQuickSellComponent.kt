package ru.astrainteractive.astrashop.gui.quicksell.presentation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astrashop.api.ShopApi
import ru.astrainteractive.astrashop.core.PluginTranslation
import ru.astrainteractive.astrashop.domain.interactor.SellInteractor
import ru.astrainteractive.astrashop.gui.model.SharedItemStack
import java.util.UUID

internal class DefaultQuickSellComponent(
    private val translation: PluginTranslation,
    private val shopApi: ShopApi,
    private val sellInteractor: SellInteractor
) : AsyncComponent(), QuickSellComponent {
    private val messageChannel = Channel<QuickSellComponent.Label>()
    override val labels: Flow<QuickSellComponent.Label> = messageChannel.receiveAsFlow()

    private val limitedDispatcher = Dispatchers.IO.limitedParallelism(1)

    override fun onItemClicked(itemStack: SharedItemStack, playerUUID: UUID, isShiftClick: Boolean) {
        componentScope.launch(limitedDispatcher) {
            val shopItemsWithConfig = shopApi.fetchShopList().mapNotNull { shopConfig ->
                val foundShopItem = shopConfig.items.values.firstOrNull { shopItem ->
                    itemStack.isSimilar(shopItem)
                } ?: return@mapNotNull null
                foundShopItem to shopConfig
            }
            val (item, shopConfig) = shopItemsWithConfig.firstOrNull() ?: run {
                val label = QuickSellComponent.Label.Message(translation.general.itemNotBuying)
                messageChannel.send(label)
                return@launch
            }
            val amount = if (isShiftClick) 64 else 1
            sellInteractor(SellInteractor.Param(amount, item, shopConfig, playerUUID))
        }
    }
}
