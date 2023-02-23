plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.github.johnrengelman.shadow")
    id("basic-plugin")
    id("basic-shadow")
    id("basic-resource-processor")
}

dependencies {
    // Kotlin
//    compileOnly(libs.kotlinGradlePlugin)
    // Coroutines
    compileOnly(libs.coroutines.coreJvm)
    compileOnly(libs.coroutines.core)
    // Serialization
    compileOnly(libs.kotlin.serialization)
    compileOnly(libs.kotlin.serializationJson)
    compileOnly(libs.kotlin.serializationKaml)
    // AstraLibs
    compileOnly(libs.astralibs.ktxCore)
    compileOnly(libs.astralibs.spigotCore)
    compileOnly(libs.astralibs.spigotGui)
    compileOnly(libs.astralibs.orm)
    // Test-Core
    testImplementation(kotlin("test-junit5"))
    testImplementation(platform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // Spigot dependencies
    compileOnly(libs.essentialsx)
    compileOnly(libs.paperApi)
    compileOnly(libs.spigotApi)
    compileOnly(libs.spigot)
    compileOnly(libs.protocollib)
    compileOnly(libs.placeholderapi)
    compileOnly(libs.worldguard.bukkit)
    compileOnly(libs.discordsrv)
    compileOnly(libs.vaultapi)
    compileOnly(libs.coreprotect)
    implementation(libs.bstats.bukkit)
    implementation(project(":domain:core"))
    implementation(project(":domain:spigot"))
}