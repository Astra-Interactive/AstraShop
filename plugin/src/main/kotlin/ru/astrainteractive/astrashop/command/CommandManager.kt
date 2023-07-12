package ru.astrainteractive.astrashop.command

import ru.astrainteractive.astralibs.Factory
import ru.astrainteractive.astrashop.AstraShop
import ru.astrainteractive.astrashop.command.di.CommandModule

class CommandManager(
    private val plugin: AstraShop,
    private val module: CommandModule
) : Factory<Unit> {

    override fun build() {
        reload(plugin, module)
        shop(plugin, module)
    }
}
