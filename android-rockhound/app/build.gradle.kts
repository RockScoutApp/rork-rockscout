plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.rork.rockscout"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.rork.rockscout"
        minSdk = 26
        targetSdk = 36
        // IMPORTANT: bump versionCode on EVERY release build (Play + sideloaded
        // APK). Android refuses to install an APK whose versionCode is lower
        // than the installed one, and the in-app updater only offers an update
        // when the server-reported code is higher. Keep this in lockstep with
        // LATEST_VERSION_CODE in functions/app-version.ts.
        versionCode = 12
        versionName = "1.1.10"

        multiDexEnabled = true
    }

    flavorDimensions += "tier"
    productFlavors {
        create("free") {
            dimension = "tier"
            buildConfigField("boolean", "FORCE_PREMIUM", "false")
        }
        create("pro") {
            dimension = "tier"
            buildConfigField("boolean", "FORCE_PREMIUM", "true")
        }
    }

    signingConfigs {
        getByName("debug") {
            // Force ALL signing schemes so sideloaded APKs install on every
            // Android device/ROM. v1 (JAR) is required by many OEM installers
            // even on API 26+. Without it, users see "App not installed."
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            // Always use debug signing as the baseline. Rork's build system
            // (runChecks) injects its own persistent managed signing key,
            // ensuring every published APK is signed with the SAME key.
            // NEVER add a custom keystore — mixing keys causes "App not installed"
            // on update because Android rejects signature mismatches.
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

// Copy the free-flavor release APK to the unflavored path that build
// tooling (runChecks) expects: app/build/outputs/apk/release/app-release.apk
// Use afterEvaluate because flavor tasks (assembleFreeRelease) are created
// during Android plugin evaluation.
project.afterEvaluate {
    tasks.register<Copy>("copyFreeReleaseToUnflavoredPath") {
        from(layout.buildDirectory.dir("outputs/apk/free/release/app-free-release.apk"))
        into(layout.buildDirectory.dir("outputs/apk/release"))
        rename { "app-release.apk" }
        mustRunAfter("assembleFreeRelease")
    }

    tasks.findByName("assembleFreeRelease")?.let {
        it.finalizedBy("copyFreeReleaseToUnflavoredPath")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.json)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.koin.androidx.compose)
    implementation(libs.revenuecat.purchases)
    implementation(libs.revenuecat.purchases.ui)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.play.review)
    implementation(libs.play.review.ktx)
    implementation(libs.play.app.update)
    implementation(libs.play.app.update.ktx)
    implementation(libs.osmdroid.android)
    implementation(libs.play.services.ads)
    implementation(libs.zxing.core)
    implementation(libs.androidx.exifinterface)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui)
    implementation(libs.media3.common)
    implementation(libs.media3.datasource)
    implementation(libs.tensorflow.lite)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    debugImplementation(libs.androidx.ui.tooling)
}
