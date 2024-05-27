package ru.astrainteractive.astrashop.command.shop

import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.command.api.command.BukkitCommand

interface ShopCommand : BukkitCommand {
    sealed interface Output {
        data object NotPlayer : Output
        data object NoPermission : Output
        data object WrongUsage : Output
        class OpenShops(val player: Player) : Output
        class OpenQuickSell(val player: Player) : Output
    }
}
