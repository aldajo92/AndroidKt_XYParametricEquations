plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.aldajo92.xyparametricequations"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.aldajo92.androidanimationscompose"

        minSdk = 24
        targetSdk = 34
        versionCode = 12
        versionName = "1.7"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {
    val coreKtxVersion = "1.13.1"
    val composeUIVersion = "1.6.7"
    val androidXLifecycleVersion = "2.8.1"

    implementation("androidx.core:core-ktx:$coreKtxVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$androidXLifecycleVersion")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.compose.ui:ui:$composeUIVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeUIVersion")
    implementation("androidx.compose.material:material:1.6.7")
    implementation("androidx.compose.runtime:runtime-livedata:$composeUIVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$androidXLifecycleVersion")
    implementation("androidx.compose.material3:material3-android:1.2.1")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.49")
    kapt("com.google.dagger:hilt-compiler:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Moshi
    implementation("com.squareup.moshi:moshi-kotlin:1.14.0")
    implementation("com.squareup.moshi:moshi-adapters:1.12.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeUIVersion")
    debugImplementation("androidx.compose.ui:ui-tooling:$composeUIVersion")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$composeUIVersion")
}
