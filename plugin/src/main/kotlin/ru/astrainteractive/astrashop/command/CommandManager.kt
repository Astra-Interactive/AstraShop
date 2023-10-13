package ru.astrainteractive.astrashop.command

import ru.astrainteractive.astrashop.AstraShop
import ru.astrainteractive.astrashop.command.di.CommandContainer
import ru.astrainteractive.klibs.kdi.Factory

class CommandManager(
    private val plugin: AstraShop,
    private val module: CommandContainer
) : Factory<Unit> {

    override fun create() {
        reload(plugin, module)
        shop(plugin, module)
    }
}
