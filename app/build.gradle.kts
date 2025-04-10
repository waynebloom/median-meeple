import com.android.build.api.dsl.Packaging

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

android {
    compileSdk = 35

    defaultConfig {
        applicationId = "com.waynebloom.scorekeeper"
        minSdk = 26
        targetSdk = 35
        versionCode = 15
        versionName = "2024.1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        room {
            schemaDirectory("$projectDir/schemas")
        }
        ksp {
            // incremental annotation processing speeds up builds
            arg("room.incremental", "true")
        }
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    sourceSets {
        // add exported schema location as test app assets.
         getByName("androidTest").assets.srcDirs("$projectDir/schemas")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            ndk.debugSymbolLevel = "FULL"
        }
        debug {
            applicationIdSuffix = ".debug"
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
        viewBinding = true
    }
    fun Packaging.() {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    namespace = "com.waynebloom.scorekeeper"
}

dependencies {

    implementation(libs.activity.compose)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.compose.material3.adaptive.navigation)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.ui.viewbinding)
    implementation(libs.constraintlayout)
    implementation(libs.datastore.preferences)
    implementation(libs.dagger.hilt)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.junit.ktx)
    implementation(libs.kotlinx.serialization)
    implementation(libs.ksp.symbol.processing.api)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.material)
    implementation(libs.navigation.compose)
    implementation(libs.play.services.ads)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    androidTestImplementation(libs.room.testing)
    androidTestImplementation(libs.runner)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
    ksp(libs.dagger.hilt.compiler)
    ksp(libs.hilt.compiler)
    ksp(libs.room.compiler)
}