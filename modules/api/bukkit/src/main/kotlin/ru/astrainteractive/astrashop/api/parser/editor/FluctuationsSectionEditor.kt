package ru.astrainteractive.astrashop.api.parser.editor

import org.bukkit.configuration.ConfigurationSection
import ru.astrainteractive.astrashop.api.model.ShopConfig
import kotlin.time.Duration.Companion.milliseconds
import ru.astrainteractive.astrashop.api.parser.editor.api.SectionEditor

object FluctuationsSectionEditor : SectionEditor<ShopConfig.Options.Fluctuations> {
    override fun read(s: ConfigurationSection): ShopConfig.Options.Fluctuations {
        return ShopConfig.Options.Fluctuations(
            enabled = s.getBoolean("enabled", false),
            period = s.getLong("period", 0L).milliseconds,
            power = s.getDouble("power", 0.1).toFloat()
        )
    }

    override fun write(value: ShopConfig.Options.Fluctuations, s: ConfigurationSection) {
        s.set("enabled", value.enabled)
        s.set("period", value.period.inWholeMilliseconds)
        s.set("power", value.power)
    }

    const val SECTION = "fluctuations"
}
