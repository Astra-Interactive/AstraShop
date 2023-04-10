package ru.astrainteractive.astrashop.gui

import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.menu.holder.PlayerHolder

data class ShopPlayerHolder(
    override val player: Player,
    var shopPage: Int = 0,
    var shopsPage: Int = 0
) : PlayerHolder