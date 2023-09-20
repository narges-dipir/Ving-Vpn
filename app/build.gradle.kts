plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
}

android {
    namespace = "com.abrnoc.application"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.abrnoc.application"
        minSdk = 26
        targetSdk = 33
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
        buildConfig = true
        compose = true
        viewBinding = true
        aidl = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(fileTree("libs"))
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(project(mapOf("path" to ":domain")))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    //base
    implementation("androidx.preference:preference-ktx:1.2.0")
    //compose
    implementation("androidx.compose.ui:ui-util:1.4.3")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.30.1")
    implementation("androidx.compose.material:material:1.4.3")
    implementation("androidx.navigation:navigation-compose:2.6.0")
    implementation("androidx.compose.foundation:foundation:1.4.3")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.44.2")
    kapt("com.google.dagger:hilt-compiler:2.44.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    implementation("dev.olshevski.navigation:reimagined-hilt:1.1.1")
    // temp
    implementation("com.google.android.material:material:1.9.0")
    implementation("com.github.daniel-stoneuk:material-about-library:3.2.0-rc01")
    implementation("androidx.room:room-runtime:2.4.2")
    kapt("androidx.room:room-compiler:2.4.2")
    implementation("androidx.room:room-ktx:2.4.2")
    implementation("com.github.MatrixDev.Roomigrant:RoomigrantLib:0.3.4")
    kapt("com.github.MatrixDev.Roomigrant:RoomigrantCompiler:0.3.4")
    implementation("androidx.work:work-runtime-ktx:2.7.1")
    implementation("androidx.work:work-multiprocess:2.7.1")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.esotericsoftware:kryo:5.2.1")
    implementation("com.takisoft.preferencex:preferencex:1.1.0")
    implementation("com.takisoft.preferencex:preferencex-simplemenu:1.1.0")
    implementation("org.yaml:snakeyaml:1.30")
    implementation("org.ini4j:ini4j:0.5.4")
    implementation("androidx.recyclerview:recyclerview:1.2.1")

    implementation("com.simplecityapps:recyclerview-fastscroll:2.0.1") {
        exclude(group = "androidx.recyclerview")
        exclude(group = "androidx.appcompat")
    }
    implementation("com.jakewharton:process-phoenix:2.1.2")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    //
    // Retrofitlt
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.5.0")
    implementation("com.github.ihsanbal:LoggingInterceptor:3.1.0") {
        exclude(group = "org.json", module = "json")
    }
    // Androidx ktx
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    kapt("androidx.hilt:hilt-compiler:1.0.0")
    // Preferences DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.0-alpha04")
    // Timber
    implementation("com.jakewharton.timber:timber:4.7.1")
    // coil
    implementation("io.coil-kt:coil:2.4.0")
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("io.coil-kt:coil-svg:2.2.2")
    // flags
}