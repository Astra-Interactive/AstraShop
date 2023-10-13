plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}
dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin)
    implementation(libs.minecraft.astralibs.ktxcore)
    implementation(libs.klibs.kdi)
    // Test
    testImplementation(libs.bundles.testing.kotlin)
    testImplementation(libs.tests.kotlin.test)
    // Local
    implementation(projects.modules.api)
}
