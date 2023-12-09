package ru.astrainteractive.astrashop.command

import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astrashop.command.di.CommandManagerDependencies
import ru.astrainteractive.astrashop.command.reload.reload
import ru.astrainteractive.astrashop.command.shop.ShopCommandFactory
import ru.astrainteractive.klibs.kdi.Factory

class CommandManager(
    module: CommandManagerDependencies,
    translationContext: BukkitTranslationContext
) : Factory<Unit>,
    CommandManagerDependencies by module,
    BukkitTranslationContext by translationContext {

    override fun create() {
        reload()
        ShopCommandFactory(this, this.plugin).create()
    }
}
