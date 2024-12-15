package ru.astrainteractive.astrashop.api.parser.editor

import org.bukkit.configuration.ConfigurationSection
import ru.astrainteractive.astrashop.api.model.ShopConfig
import kotlin.time.Duration.Companion.milliseconds
import ru.astrainteractive.astrashop.api.parser.editor.api.SectionEditor

object StabilizingSectionEditor : SectionEditor<ShopConfig.Options.Stabilizing> {
    override fun read(s: ConfigurationSection): ShopConfig.Options.Stabilizing {
        return ShopConfig.Options.Stabilizing(
            enabled = s.getBoolean("enabled", false),
            period = s.getLong("period", 0L).milliseconds,
            power = s.getDouble("power", 0.1).toFloat()
        )
    }

    override fun write(value: ShopConfig.Options.Stabilizing, s: ConfigurationSection) {
        s.set("enabled", value.enabled)
        s.set("period", value.period.inWholeMilliseconds)
        s.set("power", value.power)
    }

    const val SECTION = "stabilizing"
}
