plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("ru.astrainteractive.gradleplugin.minecraft.multiplatform")
}
minecraftMultiplatform {
    bukkit()
    dependencies {
        // Kotlin
        implementation(libs.bundles.kotlin)
        // AstraLibs
        implementation(libs.minecraft.astralibs.core)
        implementation(libs.minecraft.astralibs.orm)
        implementation(libs.klibs.kdi)
        // Test
        testImplementation(libs.bundles.testing.kotlin)
        testImplementation(libs.tests.kotlin.test)
        // Spigot dependencies
        "bukkitMainCompileOnly"(libs.minecraft.paper.api)
        "bukkitMainImplementation"(libs.minecraft.bstats)
        "bukkitMainImplementation"(libs.minecraft.astralibs.menu.bukkit)
        "bukkitMainImplementation"(libs.minecraft.astralibs.core.bukkit)
    }
}
