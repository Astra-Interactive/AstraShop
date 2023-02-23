plugins {
    java
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("basic-plugin")
}
dependencies {
    // Kotlin
    compileOnly(libs.kotlinGradlePlugin)
    // Coroutines
    compileOnly(libs.coroutines.coreJvm)
    compileOnly(libs.coroutines.core)
    // Serialization
    compileOnly(libs.kotlin.serialization)
    compileOnly(libs.kotlin.serializationJson)
    compileOnly(libs.kotlin.serializationKaml)
}