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
    implementation(libs.coroutines.coreJvm)
    implementation(libs.coroutines.core)
    // Serialization
    implementation(libs.kotlin.serialization)
    implementation(libs.kotlin.serializationJson)
    implementation(libs.kotlin.serializationKaml)
    // AstraLibs
    implementation(libs.astralibs.ktxCore)
    implementation(libs.astralibs.spigotCore)
    implementation(libs.astralibs.spigotGui)
    implementation(libs.astralibs.orm)
    implementation(libs.bstats.bukkit)
    // Spigot
    compileOnly(libs.paperApi)
//    compileOnly(libs.spigotApi)
//    compileOnly(libs.spigot)
    implementation(project(":domain:core"))
}