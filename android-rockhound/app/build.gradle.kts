import java.util.Properties
import java.io.File

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

/**
 * Persistent release keystore — loaded from keystore.properties in the project
 * root. This ensures EVERY build (local, CI, Rork) is signed with the SAME key
 * so users can update over an existing install without getting "App not installed".
 *
 * The debug keystore is auto-generated per machine and therefore produces
 * different signatures on different machines — that was the root cause of the
 * persistent "app not installed" errors on update.
 */
val keystorePropertiesFile = File(rootProject.projectDir, "keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(keystorePropertiesFile.inputStream())
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
        versionCode = 8
        versionName = "1.1.6"

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
        create("release") {
            if (keystorePropertiesFile.exists()) {
                storeFile = file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            // Use the persistent project keystore so all builds share the same
            // signing key. Falls back to debug only if the keystore is missing.
            // This is critical: if different builds are signed with different
            // keys, Android rejects the update with "App not installed".
            signingConfig = if (keystorePropertiesFile.exists()) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }
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
    debugImplementation(libs.androidx.ui.tooling)
}
