plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)

    id("kotlin-kapt")
}

android {
    namespace = "com.kiologyn.expenda"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kiologyn.expenda"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.7"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // kotlin
    implementation(libs.kotlin.reflect)

    // compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.manifest)
    implementation(libs.compose.material)
    implementation(libs.compose.material3)

    // compose navigation
    implementation(libs.navigation.compose)

    // junit
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)

    // various
    implementation(libs.core.ktx)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)

    // room persistent library
    implementation(libs.room.ktx)
    implementation(libs.room.runtime)
    kapt(libs.room.compiler)

    // DataStore
    implementation(libs.datastore)
    implementation(libs.datastore.core)
    implementation(libs.datastore.preferences)

    // third-party
    implementation(libs.philjay.mpandroidchart)
}