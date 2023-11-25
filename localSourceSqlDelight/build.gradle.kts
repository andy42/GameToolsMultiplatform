plugins {
    kotlin("multiplatform")
    //kotlin("native.cocoapods")
    id("com.android.library")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization") version "1.9.10"
    id("app.cash.sqldelight") version "2.0.0"
}

val kodeinVersion = findProperty("kodein.version") as String
val coroutinesVersion = findProperty("coroutinesVersion.version") as String

kotlin {
    androidTarget()

    jvm("desktop")

    //iosX64()
    //iosArm64()
    //iosSimulatorArm64()

//    cocoapods {
//        version = "1.0.0"
//        summary = "Some description for the Shared Module"
//        homepage = "Link to the Shared Module homepage"
//        ios.deploymentTarget = "14.1"
//        podfile = project.file("../iosApp/Podfile")
//        framework {
//            baseName = "shared"
//            isStatic = true
//        }
//        extraSpecAttributes["resources"] = "['src/commonMain/resources/**', 'src/iosMain/resources/**']"
//    }

    sourceSets {
        val commonMain by getting {
            dependencies {
//                implementation(compose.runtime)
//                implementation(compose.foundation)
//                implementation(compose.material)
//                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
//                implementation(compose.components.resources)
//
//                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:$coroutinesVersion")
//
//                implementation("org.kodein.di:kodein-di-framework-compose:$kodeinVersion")
//                implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")
//                implementation("cafe.adriel.voyager:voyager-bottom-sheet-navigator:$voyagerVersion")
//                implementation("cafe.adriel.voyager:voyager-tab-navigator:$voyagerVersion")
//                implementation("cafe.adriel.voyager:voyager-transitions:$voyagerVersion")
//                implementation("cafe.adriel.voyager:voyager-kodein:$voyagerVersion")
//                implementation ("com.google.code.gson:gson:2.8.9")
//
//                implementation ("com.auth0:java-jwt:4.4.0")
//
//                implementation("io.ktor:ktor-client-apache5:$ktorVersion")
//                implementation("media.kamel:kamel-image:$mediaKamelversion")
//
//                implementation("org.jetbrains.compose.ui:ui-tooling-preview:$composeVersion")
                implementation(project(":common"))
                implementation("org.kodein.di:kodein-di-framework-compose:$kodeinVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
                implementation("com.squareup.sqldelight:runtime:1.5.5")
                implementation("app.cash.sqldelight:primitive-adapters:2.0.0-alpha05")
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.activity:activity-compose:1.7.2")
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.12.0")
                implementation("app.cash.sqldelight:android-driver:2.0.0")
            }
        }
//        val iosX64Main by getting
//        val iosArm64Main by getting
//        val iosSimulatorArm64Main by getting
//        val iosMain by creating {
//            dependsOn(commonMain)
//            iosX64Main.dependsOn(this)
//            iosArm64Main.dependsOn(this)
//            iosSimulatorArm64Main.dependsOn(this)
//            implementation("app.cash.sqldelight:native-driver:2.0.0")
//        }
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.common)
                implementation("app.cash.sqldelight:sqlite-driver:2.0.0")
            }
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "com.jaehl.gameTool.localSourceSqlDelight"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        jvmToolchain(11)
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("com.jaehl.gameTool.localSourceSqlDelight")
        }
    }
}