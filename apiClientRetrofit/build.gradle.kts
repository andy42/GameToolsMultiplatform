
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

val kodeinVersion = findProperty("kodein.version") as String

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"
}

kotlin {
    jvm()
    sourceSets {
        val jvmMain by getting  {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(project(":common"))

                implementation("org.kodein.di:kodein-di-framework-compose:$kodeinVersion")

                implementation("com.google.code.gson:gson:2.10.1")
                implementation("com.squareup.okhttp3:okhttp:4.10.0")
                implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

                implementation("com.squareup.retrofit2:retrofit:2.9.0")
                implementation("com.squareup.retrofit2:converter-gson:2.9.0")
                implementation("com.squareup.retrofit2:adapter-rxjava3:2.9.0")

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

            }
        }
    }
}