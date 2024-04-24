buildscript {
    dependencies {
        classpath(libs.kotlin.serialization)
    }
}

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
}