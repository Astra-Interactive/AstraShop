package ru.astrainteractive.astrashop.command

import ru.astrainteractive.astrashop.command.di.CommandManagerDependencies
import ru.astrainteractive.astrashop.command.reload.reload
import ru.astrainteractive.astrashop.command.shop.ShopCommandRegistry
import ru.astrainteractive.klibs.kdi.Factory

class CommandManager(
    module: CommandManagerDependencies,
) : Factory<Unit>,
    CommandManagerDependencies by module {

    override fun create() {
        reload()
        ShopCommandRegistry(this, this.plugin).register()
    }
}
