plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("androidx.navigation.safeargs")
}

android {
    namespace = "com.usi.hikemap"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.usi.hikemap"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
    }

}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    implementation("androidx.navigation:navigation-fragment:2.7.4")
    implementation("androidx.navigation:navigation-ui:2.7.4")
    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Google
    implementation("com.google.android.gms:play-services-auth:20.1.0")
    implementation("com.google.android.gms:play-services-location:15.0.1")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.5.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth:21.0.1")
    implementation("com.google.firebase:firebase-firestore:23.0.3")
    implementation("com.google.firebase:firebase-storage:20.0.0")
    implementation("com.google.firebase:firebase-database:20.0.3")

    //implementation ccp
    implementation("com.hbb20:ccp:2.7.3")

    //airbnb
    implementation("com.airbnb.android:lottie:3.4.0")

    // Maps SDK for Android
    implementation("com.google.android.gms:play-services-maps:18.2.0")

    // Bottom Navigation Bar
    implementation("com.etebarian:meow-bottom-navigation:1.2.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.61")

    //implementation Circle view
    implementation("de.hdodenhof:circleimageview:3.1.0")

    //implementation Glide
    implementation("com.github.bumptech.glide:glide:4.14.2")
    annotationProcessor("com.github.bumptech.glide:compiler:4.14.2")

    //implementation AnyChart
    implementation ("com.github.AnyChart:AnyChart-Android:1.1.5")




}