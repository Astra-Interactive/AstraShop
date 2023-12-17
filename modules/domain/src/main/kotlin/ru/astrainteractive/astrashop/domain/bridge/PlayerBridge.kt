package ru.astrainteractive.astrashop.domain.bridge

import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astrashop.api.model.ShopConfig
import java.util.UUID

interface PlayerBridge {
    /**
     * Returns a player name
     */
    fun getName(uuid: UUID): String

    /**
     * Sends a message to player
     */
    fun sendMessage(uuid: UUID, stringDesc: StringDesc)

    /**
     * Gives or drop not fitted items on floor
     *
     * @return amount of not fitted items
     */
    suspend fun giveOrDropItems(uuid: UUID, item: ShopConfig.ShopItem, amount: Int): Int

    /**
     * Removes shop item from player inventory
     *
     * @return amount of items could not be removed of null if item not found
     */
    fun removeItem(uuid: UUID, item: ShopConfig.ShopItem, amount: Int): Int?
}
