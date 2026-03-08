import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)

    alias(libs.plugins.kotlinSerialization)
    id("app.cash.sqldelight")

}

sqldelight {
    databases {
        create("HealthDatabase") {
            packageName.set("com.example.healthassistant.db")
        }
    }
}

kotlin {
    androidTarget ({
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    })
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    jvm()
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)

            implementation("io.ktor:ktor-client-okhttp:2.3.7")
            implementation("app.cash.sqldelight:android-driver:2.0.1")

            implementation("io.coil-kt:coil-compose:2.4.0")

            implementation("com.google.android.gms:play-services-location:21.0.1")

            implementation("com.google.mlkit:translate:17.0.2")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

            implementation("androidx.activity:activity-compose:1.8.0")
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(compose.materialIconsExtended)

            // Ktor core
            implementation("io.ktor:ktor-client-core:2.3.7")

            implementation("io.ktor:ktor-client-logging:2.3.7")


            // JSON serialization
            implementation("io.ktor:ktor-client-content-negotiation:2.3.7")
            implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")

            // Kotlinx serialization
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

            //SQL Delight
            implementation("app.cash.sqldelight:runtime:2.0.1")

            // Date and Time
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")

        }
        iosMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:2.3.7")
            implementation("app.cash.sqldelight:native-driver:2.0.1")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)

            implementation("org.vosk:vosk:0.3.45")
            implementation("app.cash.sqldelight:sqlite-driver:2.0.1")

        }
    }
}

android {
    namespace = "com.example.healthassistant"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.healthassistant"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")

        if (localPropertiesFile.exists()) {
            localProperties.load(localPropertiesFile.inputStream())
        }

        val newsApiKey = localProperties.getProperty("NEWS_API_KEY") ?: ""


        buildConfigField(
            "String",
            "NEWS_API_KEY",
            "\"$newsApiKey\""
        )


    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "com.example.healthassistant.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.example.healthassistant"
            packageVersion = "1.0.0"
        }
    }
}
