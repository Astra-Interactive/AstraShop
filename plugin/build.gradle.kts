
import ru.astrainteractive.gradleplugin.setupSpigotProcessor
import ru.astrainteractive.gradleplugin.setupSpigotShadow

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("ru.astrainteractive.gradleplugin.minecraft.multiplatform")
}

minecraftMultiplatform {
    dependencies {
        // Kotlin
        implementation(libs.bundles.kotlin)
        // klibs
        implementation(libs.klibs.mikro)
        // AstraLibs
        implementation(libs.minecraft.astralibs.core)
        implementation(libs.minecraft.astralibs.orm)
        implementation(libs.klibs.kdi)
        implementation(libs.minecraft.astralibs.menu.bukkit)
        implementation(libs.minecraft.astralibs.core.bukkit)
        implementation(libs.minecraft.astralibs.command)
        implementation(libs.minecraft.astralibs.command.bukkit)
        // Test
        testImplementation(libs.bundles.testing.kotlin)
        testImplementation(libs.tests.kotlin.test)
        // Spigot dependencies
        compileOnly(libs.minecraft.paper.api)
        implementation(libs.minecraft.bstats)
        compileOnly(libs.minecraft.papi)
        compileOnly(libs.minecraft.vaultapi)
        implementation(libs.minecraft.bstats)
        // Local
        implementation(projects.modules.api)
        implementation(projects.modules.core)
        implementation(projects.modules.domain)
        implementation(projects.modules.api.bukkitMain)
    }
}
val destination = File("D:\\Minecraft Servers\\Servers\\conf.smp\\smp\\plugins")
    .takeIf(File::exists)
    ?: File(rootDir, "jars")

setupSpigotShadow(destination)
setupSpigotProcessor()
