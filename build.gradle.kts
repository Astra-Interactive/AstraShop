group = libs.versions.group.get()
version = libs.versions.plugin.get()
description = libs.versions.description.get()

plugins {
    java
    `maven-publish`
    `java-library`
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.shadow) apply false
}

