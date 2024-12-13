plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(libs.plugins.klibs.minecraft.shadow)
    alias(libs.plugins.klibs.minecraft.resource.processor)
}

dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin)
    // klibs
    implementation(libs.klibs.mikro)
    // AstraLibs
    implementation(libs.minecraft.astralibs.core)
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
    implementation(projects.modules.api.main)
    implementation(projects.modules.api.bukkit)
    implementation(projects.modules.core.main)
    implementation(projects.modules.core.bukkit)
    implementation(projects.modules.domain.main)
    implementation(projects.modules.domain.bukkit)
    implementation(projects.modules.gui.main)
    implementation(projects.modules.gui.bukkit)
}

minecraftProcessResource {
    spigotResourceProcessor.process()
}

setupShadow {
    destination = File("/run/media/makeevrserg/WDGOLD2TB/MinecraftServers/Servers/conf.smp/smp/plugins")
        .takeIf { it.exists() }
        ?: File(rootDir, "jars")
    configureDefaults()
    requireShadowJarTask {
        minimize {
            exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib:${libs.versions.kotlin.version.get()}"))
        }
    }
}
