plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdk = 31

    defaultConfig {
        minSdk = 21
        targetSdk = 30
        versionCode = 1
        versionName = "1.0.0"
        applicationId = "io.github.bexonpak.regions"
        buildConfigField(
            "String",
            "PREFERENCES_FILE_NAME",
            "\"pref.xml\""
        )

        buildConfigField(
            "String",
            "DATABASE_FILE_NAME",
            "\"locale.db\""
        )

        sourceSets {
            // Adds exported schema location as test app assets.
            getByName("androidTest").assets.srcDirs("$projectDir/schemas")
        }

        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
            }
        }

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
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    val preference_version = "1.2.1"
    val lifecycleVersion = "2.4.0"
    val fragmentVersion = "1.4.0-alpha10"
    val coroutinesVersion = "1.6.4"

    implementation(
        fileTree(
            mapOf(
                "dir" to "libs",
                "include" to listOf("*.aar", "*.jar"),
                "exclude" to listOf<String>()
            )
        )
    )

    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.1")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.preference:preference-ktx:$preference_version")
    implementation("com.google.code.gson:gson:2.10.1")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycleVersion")

    implementation("androidx.fragment:fragment-ktx:$fragmentVersion")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx2:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:$coroutinesVersion")

    implementation(project(mapOf("path" to ":lib")))

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}
