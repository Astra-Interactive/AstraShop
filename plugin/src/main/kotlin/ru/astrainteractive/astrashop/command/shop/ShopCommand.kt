package ru.astrainteractive.astrashop.command.shop

import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.command.api.Command
import ru.astrainteractive.astrashop.command.shop.ShopCommand.Output

interface ShopCommand : Command<Output, Output> {
    sealed interface Output {
        data object NotPlayer : Output
        data object NoPermission : Output
        data object WrongUsage : Output
        class OpenShops(val player: Player) : Output
        class OpenQuickSell(val player: Player) : Output
    }
}
