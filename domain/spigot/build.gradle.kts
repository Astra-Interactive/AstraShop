plugins {
    java
    kotlin("jvm") version Dependencies.Kotlin.version
    kotlin("plugin.serialization") version Dependencies.Kotlin.version
}
java {
    java.sourceCompatibility = JavaVersion.VERSION_1_8
    java.targetCompatibility = JavaVersion.VERSION_17
}
repositories {
    mavenLocal()
    mavenCentral()
    maven(Dependencies.Repositories.extendedclip)
    maven(Dependencies.Repositories.maven2Apache)
    maven(Dependencies.Repositories.essentialsx)
    maven(Dependencies.Repositories.enginehub)
    maven(Dependencies.Repositories.spigotmc)
    maven(Dependencies.Repositories.dmulloy2)
    maven(Dependencies.Repositories.papermc)
    maven(Dependencies.Repositories.dv8tion)
    maven(Dependencies.Repositories.playpro)
    maven(Dependencies.Repositories.jitpack)
    maven(Dependencies.Repositories.scarsz)
    maven(Dependencies.Repositories.maven2)
    modelEngige(project)
    paperMC(project)
}
dependencies {
    // Kotlin
    implementation(Dependencies.Libraries.kotlinGradlePlugin)
    // Coroutines
    implementation(Dependencies.Libraries.kotlinxCoroutinesCoreJVM)
    implementation(Dependencies.Libraries.kotlinxCoroutinesCore)
    // Serialization
    implementation(Dependencies.Libraries.kotlinxSerialization)
    implementation(Dependencies.Libraries.kotlinxSerializationJson)
    implementation(Dependencies.Libraries.kotlinxSerializationYaml)
    // Spigot
    compileOnly(Dependencies.Libraries.paperMC)
    compileOnly(Dependencies.Libraries.spigot)
    compileOnly(Dependencies.Libraries.spigotApi)
    implementation(project(":domain:core"))
    // AstraLibs
    implementation(Dependencies.Libraries.astraLibsKtxCore)
    implementation(Dependencies.Libraries.astraLibsSpigotCore)
}