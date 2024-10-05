package ru.astrainteractive.astrashop.command

import ru.astrainteractive.astrashop.command.di.CommandManagerDependencies
import ru.astrainteractive.astrashop.command.reload.reload
import ru.astrainteractive.astrashop.command.shop.ShopCommandRegistry

class CommandManager(
    module: CommandManagerDependencies,
) : CommandManagerDependencies by module {

    fun create() {
        reload()
        ShopCommandRegistry(this, this.plugin).register()
    }
}
