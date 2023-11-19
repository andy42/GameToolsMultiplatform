plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jetbrains.compose")
}

val kodeinVersion = findProperty("kodein.version") as String
val materialVersion = findProperty("material.version") as String
val ktorVersion = findProperty("ktor.version") as String

kotlin {
    androidTarget()
    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(project(":common"))
                implementation(project(":apiClientRetrofit"))
                implementation(project(":apiClientKtor"))

                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)

                implementation("org.kodein.di:kodein-di-framework-compose:$kodeinVersion")
                implementation("androidx.datastore:datastore-preferences:1.0.0")

                implementation("io.ktor:ktor-client-android:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
            }
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "com.jaehl.gameTool"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].java.srcDirs("src/androidMain/java")

    packagingOptions { resources.excludes.add("META-INF/*") }
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    defaultConfig {
        applicationId = "com.jaehl.gameTool.GameTool"
        minSdk = (findProperty("android.minSdk") as String).toInt()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        jvmToolchain(11)
    }
}

dependencies {
    implementation("io.ktor:ktor-client-android:2.3.3")
    androidTestImplementation("androidx.test:core:1.6.0-alpha01")
    androidTestImplementation("androidx.test.ext:junit:1.2.0-alpha01")
    androidTestImplementation("androidx.test:runner:1.6.0-alpha03")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.3.0-alpha03")
}
