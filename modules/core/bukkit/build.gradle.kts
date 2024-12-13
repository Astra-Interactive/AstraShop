plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin)
    // AstraLibs
    implementation(libs.minecraft.astralibs.core)
    implementation(libs.minecraft.astralibs.menu.bukkit)
    implementation(libs.minecraft.astralibs.core.bukkit)
    implementation(libs.klibs.mikro)
    // Spigot dependencies
    compileOnly(libs.minecraft.paper.api)
//    implementation(libs.minecraft.bstats)
//    compileOnly(libs.minecraft.papi)
    compileOnly(libs.minecraft.vaultapi)
//    implementation(libs.minecraft.bstats)
    // Local
    implementation(projects.modules.core.main)
}
