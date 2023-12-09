package ru.astrainteractive.astrashop.gui.model

import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.menu.holder.PlayerHolder

data class ShopPlayerHolder(
    override val player: Player,
    val shopPage: Int = 0,
    val shopsPage: Int = 0
) : PlayerHolder
