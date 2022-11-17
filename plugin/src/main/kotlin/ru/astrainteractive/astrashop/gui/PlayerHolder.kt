package ru.astrainteractive.astrashop.gui

import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.menu.IPlayerHolder

class PlayerHolder(
    override val player: Player,
    var shopPage: Int = 0,
    var shopsPage: Int = 0
) : IPlayerHolder