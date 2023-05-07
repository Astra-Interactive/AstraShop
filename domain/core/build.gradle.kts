plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("basic-java")
}
dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin)
    // Test
    testImplementation(libs.bundles.testing.kotlin)
    testImplementation(libs.tests.kotlin.test)
}
