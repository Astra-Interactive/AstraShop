package ru.astrainteractive.astrashop.command

import ru.astrainteractive.astrashop.AstraShop
import ru.astrainteractive.astrashop.command.di.CommandModule
import ru.astrainteractive.klibs.kdi.Factory

class CommandManager(
    private val plugin: AstraShop,
    private val module: CommandModule
) : Factory<Unit> {

    override fun create() {
        reload(plugin, module)
        shop(plugin, module)
    }
}
