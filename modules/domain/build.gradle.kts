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
        implementation(libs.minecraft.astralibs.ktxcore)
        implementation(libs.minecraft.astralibs.orm)
        implementation(libs.klibs.kdi)
        implementation(libs.minecraft.astralibs.spigot.gui)
        implementation(libs.minecraft.astralibs.spigot.core)
        // Spigot dependencies
        compileOnly(libs.minecraft.paper.api)
        implementation(libs.minecraft.bstats)
        compileOnly(libs.minecraft.papi)
        compileOnly(libs.minecraft.vaultapi)
        implementation(libs.minecraft.bstats)
        // Local
        implementation(projects.modules.api)
        implementation(projects.modules.api.bukkitMain)
        implementation(projects.modules.core)
    }
}