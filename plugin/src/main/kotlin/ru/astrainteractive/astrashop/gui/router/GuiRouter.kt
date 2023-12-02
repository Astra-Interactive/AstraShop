package ru.astrainteractive.astrashop.gui.router

import kotlinx.coroutines.withContext
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.menu.menu.Menu

interface GuiRouter {
    sealed interface Route {
        class Buy : Route
        class QuickSell : Route
        class Shop : Route
        class Shops : Route
    }

    suspend fun open(player: Player, route: Route)
}

class GuiRouterImpl(
    private val dispatchers: BukkitDispatchers
) : GuiRouter {

    override suspend fun open(player: Player, route: GuiRouter.Route) {
        val menu: Menu = withContext(dispatchers.BukkitAsync) {
            when (route) {
                is GuiRouter.Route.Buy -> TODO()
                is GuiRouter.Route.QuickSell -> TODO()
                is GuiRouter.Route.Shop -> TODO()
                is GuiRouter.Route.Shops -> TODO()
            }
        }
        withContext(dispatchers.BukkitMain) {
            menu.open()
        }
    }
}
