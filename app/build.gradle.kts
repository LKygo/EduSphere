    @Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
    id("kotlin-kapt")

}

android {
    namespace = "com.kygoinc.edusphere"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kygoinc.edusphere"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.activity)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.androidx.ui.text.android)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage.ktx)
//    implementation(libs.androidx.room)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Import the BoM for the Firebase platform
//    implementation (platform("com.google.firebase:firebase-bom:31.2.3"))
//    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.0"))

    //  Lottie files
    implementation (libs.lottie)

//    Room DB
//    implementation(libs.androidx.room.runtime)
//    implementation (libs.androidx.room.ktx)
//    annotationProcessor(libs.androidx.room.compiler)

    implementation ("androidx.room:room-runtime:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1")
    kapt ("androidx.room:room-compiler:2.6.1")

//    Coroutines
    implementation (libs.kotlinx.coroutines.android)

    // Firebase dependencies
//    implementation 'com.google.firebase:firebase-core'
//    implementation ("com.google.firebase:firebase-auth")
//    implementation ("com.google.firebase:firebase-storage-ktx")
//    implementation ("com.google.firebase:firebase-database-ktx")
//    implementation ("com.google.firebase:firebase-messaging")
//    implementation ('com.google.firebase:firebase-database:20.3.0)

    // Scalable size unit  for different screen sizes
    implementation (libs.sdp.android)
    implementation (libs.ssp.android)
//    Other dependencies
    implementation (libs.circleimageview)
    implementation (libs.picasso)
    implementation (libs.androidx.cardview)
    implementation (libs.glide)
    annotationProcessor (libs.compiler)
}