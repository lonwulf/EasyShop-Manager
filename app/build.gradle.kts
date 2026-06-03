plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.sql.delight)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.kotzilla)
}

android {
    namespace = "com.lonwulf.labs.easyshopmanager"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.lonwulf.labs.easyshopmanager"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters.add("armeabi-v7a")
            abiFilters.add("arm64-v8a") // Keep 64 for other devices
        }
    }

    buildTypes {
        getByName("release") {
//            signingConfig = signingConfigs.getByName("release")
            multiDexKeepProguard = file("multidex-config.pro")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
        getByName("debug") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin{
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(project(":camera-lib"))
    implementation(project(":auth"))
    implementation(project(":presentation"))
    implementation(project(":navigation"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(libs.navigation.compose)

    implementation(libs.permissions)
    implementation(libs.cameraView)
    implementation(libs.sql.delight)
    implementation(libs.sql.delight.coroutines)
    implementation(libs.sql.delight.paging)
    implementation(libs.dataStore)
    implementation(libs.kotlin.collections)
    implementation(libs.kotlin.serialization)
    implementation(libs.work.manager)
    implementation(libs.koin.android)
//    implementation(libs.koin.core)
    implementation(libs.koin.compose.navigation)
//    implementation(libs.koin.viewmodel)
    implementation(libs.koin.workmanager)
    implementation(libs.kotzilla.sdk.compose)

    implementation(libs.ktor.client.android)
    implementation(libs.ktor.mock)
    implementation(libs.ktor.logging)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization)
    implementation(libs.android.multidex)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

sqldelight {
    databases {
        create("Catalogue") {
            packageName.set("com.lonwulf.labs.easyshopmanager.db")
            dialect(libs.sql.delight.dialect)
        }
    }
}