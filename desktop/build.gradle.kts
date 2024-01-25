import org.jetbrains.compose.desktop.application.dsl.TargetFormat

val kodeinVersion = findProperty("kodein.version") as String
val ktorVersion = findProperty("ktor.version") as String
val mediaKamelversion = findProperty("mediaKamel.version") as String

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization") version "1.9.10"
}

kotlin {
    jvm()
    sourceSets {
        val jvmMain by getting  {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(project(":common"))
                implementation(project(":apiClientRetrofit"))
                implementation(project(":apiClientKtor"))
                implementation(project(":localSourceSqlDelight"))

                implementation("org.kodein.di:kodein-di-framework-compose:$kodeinVersion")


                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")


                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
                implementation("media.kamel:kamel-image:$mediaKamelversion")
                implementation("io.ktor:ktor-client-okhttp-jvm:$ktorVersion")

            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.jaehl.gameTool.desktop.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "KotlinMultiplatformComposeDesktopApplication"
            packageVersion = "1.0.0"
            modules("jdk.unsupported")
        }
    }
}
