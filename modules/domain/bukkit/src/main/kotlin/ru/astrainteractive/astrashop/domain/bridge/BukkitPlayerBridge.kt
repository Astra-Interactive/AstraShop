package ru.astrainteractive.astrashop.domain.bridge

import dev.lone.itemsadder.api.CustomStack
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astrashop.api.model.ShopConfig
import ru.astrainteractive.astrashop.api.model.SpigotShopItemStack
import ru.astrainteractive.astrashop.core.util.getValue
import ru.astrainteractive.astrashop.domain.util.ItemStackExt.copy
import ru.astrainteractive.astrashop.domain.util.ItemStackExt.toItemStack
import ru.astrainteractive.klibs.kstorage.api.Krate
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import java.util.UUID

internal class BukkitPlayerBridge(
    private val kyoriComponentSerializerKrate: Krate<KyoriComponentSerializer>,
    private val dispatchers: KotlinDispatchers
) : PlayerBridge {
    private val kyoriComponentSerializer by kyoriComponentSerializerKrate

    private fun UUID.toPlayer() = Bukkit.getPlayer(this) ?: error("Player not found")

    override fun getName(uuid: UUID): String {
        return uuid.toPlayer().name
    }

    override fun sendMessage(uuid: UUID, stringDesc: StringDesc) {
        val player = uuid.toPlayer()
        kyoriComponentSerializer.toComponent(stringDesc).run(player::sendMessage)
    }

    override suspend fun giveOrDropItems(uuid: UUID, item: ShopConfig.ShopItem, amount: Int): Int {
        val player = uuid.toPlayer()
        val itemStack = item.toItemStack().copy(amount)
        val notFittedItems = player.inventory.addItem(itemStack)
        if (notFittedItems.isNotEmpty()) {
            withContext(dispatchers.Main) {
                player.location.world.dropItemNaturally(player.location, itemStack.copy(notFittedItems.size))
            }
        }
        return notFittedItems.size
    }

    override fun removeItem(uuid: UUID, item: ShopConfig.ShopItem, amount: Int): Int? {
        val player = uuid.toPlayer()
        val itemStack = when (val shopItem = item.shopItem) {
            is SpigotShopItemStack.ItemStackStack -> {
                if (!player.inventory.contains(shopItem.itemStack)) {
                    return null
                }
                shopItem.itemStack
            }

            is SpigotShopItemStack.Material -> {
                if (!player.inventory.contains(shopItem.material)) {
                    return null
                }
                ItemStack(shopItem.material)
            }

            is SpigotShopItemStack.ItemsAdder -> {
                val itemStack = CustomStack.getInstance(shopItem.namespaceId)?.itemStack
                itemStack ?: error("Could not find item ${shopItem.namespaceId}")
                if (!player.inventory.contains(itemStack)) {
                    return null
                }
                itemStack
            }

            else -> error("Not spigot item")
        }
        return player.inventory.removeItem(itemStack.copy(amount)).map { it.value.amount }.sum()
    }
}
