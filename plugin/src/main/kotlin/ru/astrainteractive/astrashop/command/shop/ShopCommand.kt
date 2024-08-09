package ru.astrainteractive.astrashop.command.shop

import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.command.api.exception.CommandException

interface ShopCommand {
    sealed interface Output {
        class OpenShops(val player: Player) : Output
        class OpenQuickSell(val player: Player) : Output
    }

    sealed class Error(message: String) : CommandException(message) {
        data object NotPlayer : Error("Not player")
        data object WrongUsage : Error("Wrong usage")
    }
}
