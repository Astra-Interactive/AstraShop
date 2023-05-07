package ru.astrainteractive.astrashop.commands

import ru.astrainteractive.astralibs.Factory
import ru.astrainteractive.astrashop.AstraShop
import ru.astrainteractive.astrashop.commands.di.CommandsModule

class CommandManager(
    private val plugin: AstraShop,
    private val module: CommandsModule
) : Factory<Unit> {

    override fun build() {
        reload(plugin, module)
        shop(plugin, module)
    }
}
