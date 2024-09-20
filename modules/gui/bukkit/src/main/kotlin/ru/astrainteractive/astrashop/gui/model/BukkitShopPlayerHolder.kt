package ru.astrainteractive.astrashop.gui.model

import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.menu.holder.PlayerHolder
import java.util.UUID

data class BukkitShopPlayerHolder(
    override val player: Player,
    override val shopPage: Int = 0,
    override val shopsPage: Int = 0
) : ShopPlayerHolder, PlayerHolder {
    override val playerUUID: UUID
        get() = player.uniqueId
}
