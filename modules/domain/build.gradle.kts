plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("ru.astrainteractive.gradleplugin.minecraft.multiplatform")
}

minecraftMultiplatform {
    dependencies {
        // Kotlin
        implementation(libs.bundles.kotlin)
        // AstraLibs
        implementation(libs.minecraft.astralibs.core)
        implementation(libs.minecraft.astralibs.orm)
        implementation(libs.klibs.kdi)
        implementation(libs.minecraft.astralibs.menu.bukkit)
        implementation(libs.minecraft.astralibs.core.bukkit)
        // Spigot dependencies
        compileOnly(libs.minecraft.paper.api)
        implementation(libs.minecraft.bstats)
        compileOnly(libs.minecraft.papi)
        compileOnly(libs.minecraft.vaultapi)
        implementation(libs.minecraft.bstats)
        compileOnly("com.github.LoneDev6:API-ItemsAdder:3.6.1")
        // Test
        testImplementation(libs.bundles.testing.kotlin)
        testImplementation(libs.tests.kotlin.test)
        // Local
        implementation(projects.modules.api)
        implementation(projects.modules.api.bukkitMain)
        implementation(projects.modules.core)
    }
}
