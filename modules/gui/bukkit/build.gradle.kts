plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin)
    // AstraLibs
    implementation(libs.minecraft.astralibs.core)
    implementation(libs.minecraft.astralibs.orm)
    implementation(libs.klibs.kdi)
    implementation(libs.klibs.mikro)
    implementation(libs.minecraft.astralibs.menu.bukkit)
    implementation(libs.minecraft.astralibs.core.bukkit)
    // Spigot dependencies
    compileOnly(libs.minecraft.paper.api)
//    implementation(libs.minecraft.bstats)
//    compileOnly(libs.minecraft.papi)
//    compileOnly(libs.minecraft.vaultapi)
//    implementation(libs.minecraft.bstats)
    compileOnly("com.github.LoneDev6:API-ItemsAdder:3.6.3-beta-14")
    // Test
    testImplementation(libs.bundles.testing.kotlin)
    testImplementation(libs.tests.kotlin.test)
    // Local
    implementation(projects.modules.api.main)
    implementation(projects.modules.api.bukkit)
    implementation(projects.modules.core.main)
    implementation(projects.modules.core.bukkit)
    implementation(projects.modules.domain.main)
    implementation(projects.modules.domain.bukkit)
    implementation(projects.modules.gui.main)
}
