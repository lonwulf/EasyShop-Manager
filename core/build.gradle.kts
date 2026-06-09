plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.sql.delight)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.lonwulf.labs.easyshopmanager.core"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    buildTypes {
        getByName("release") {
            multiDexKeepProguard = file("multidex-config.pro")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
        getByName("debug") {
            isMinifyEnabled = false
        }
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)

    implementation(libs.sql.delight)
    implementation(libs.sql.delight.coroutines)
    implementation(libs.sql.delight.paging)
    implementation(libs.dataStore)
    implementation(libs.kotlin.collections)
    implementation(libs.kotlin.serialization)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.mock)
    implementation(libs.ktor.logging)
    implementation(libs.ktor.client.content.negotiation)
    api(libs.ktor.serialization)

    implementation(libs.koin.android)
//    implementation(libs.koin.core)
    implementation(libs.koin.compose.navigation)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

sqldelight {
    databases {
        create("Catalogue") {
            packageName.set("com.lonwulf.labs.easyshopmanager.db")
            dialect(libs.sql.delight.dialect)
        }
    }
}