package ru.astrainteractive.astrashop.command.shop

import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.command.api.executor.CommandExecutor
import ru.astrainteractive.astrashop.command.di.CommandManagerDependencies
import ru.astrainteractive.astrashop.gui.model.ShopPlayerHolder
import ru.astrainteractive.astrashop.gui.router.GuiRouter

class ShopCommandExecutor(
    dependencies: CommandManagerDependencies
) : CommandExecutor<ShopCommand.Output>,
    CommandManagerDependencies by dependencies {
    override fun execute(input: ShopCommand.Output) {
        when (input) {
            is ShopCommand.Output.OpenQuickSell -> scope.launch(dispatchers.Main) {
                val route = GuiRouter.Route.QuickSell(input.player)
                router.open(route)
            }

            is ShopCommand.Output.OpenShops -> scope.launch(dispatchers.BukkitAsync) {
                val route = GuiRouter.Route.Shops(ShopPlayerHolder(input.player))
                router.open(route)
            }

            else -> Unit
        }
    }
}
