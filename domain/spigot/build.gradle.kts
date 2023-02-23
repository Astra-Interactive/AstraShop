plugins {
    java
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("basic-plugin")
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
    compileOnly(libs.bstats.bukkit)
    // Spigot
    compileOnly(libs.paperApi)
    compileOnly(libs.spigotApi)
    compileOnly(libs.spigot)
    implementation(project(":domain:core"))
}