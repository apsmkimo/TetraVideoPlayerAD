// SMCPKG_SUPPORT>>>Cursor031
import java.util.Properties
// SMCPKG_SUPPORT<<<Cursor031

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

// SMCPKG_SUPPORT>>>Cursor031

/** Google official test IDs — committed defaults; always used for debug. */
val TEST_ADMOB_APP_ID = "ca-app-pub-3940256099942544~3347511713"
val TEST_ADMOB_BANNER_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
val TEST_ADMOB_INTERSTITIAL_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

fun loadLocalProperties(): Properties {
    val props = Properties()
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use { props.load(it) }
    }
    return props
}

fun secretOrLocal(localProps: Properties, key: String): String? {
    val fromEnv = System.getenv(key)?.trim()?.takeIf { it.isNotEmpty() }
    if (fromEnv != null) {
        return fromEnv
    }
    return localProps.getProperty(key)?.trim()?.takeIf { it.isNotEmpty() }
}

fun quoteForBuildConfig(value: String): String {
    return "\"${value.replace("\\", "\\\\").replace("\"", "\\\"")}\""
}

fun com.android.build.api.dsl.VariantDimension.applyAdMobIds(
    appId: String,
    bannerUnitId: String,
    interstitialUnitId: String,
) {
    buildConfigField("String", "ADMOB_APP_ID", quoteForBuildConfig(appId))
    buildConfigField("String", "ADMOB_BANNER_UNIT_ID", quoteForBuildConfig(bannerUnitId))
    buildConfigField("String", "ADMOB_INTERSTITIAL_UNIT_ID", quoteForBuildConfig(interstitialUnitId))
    manifestPlaceholders["admobAppId"] = appId
}

val localProps = loadLocalProperties()
val releaseAdMobAppId = secretOrLocal(localProps, "ADMOB_APP_ID")
val releaseAdMobBannerUnitId = secretOrLocal(localProps, "ADMOB_BANNER_UNIT_ID")
val releaseAdMobInterstitialUnitId = secretOrLocal(localProps, "ADMOB_INTERSTITIAL_UNIT_ID")
val hasReleaseAdMobIds =
    !releaseAdMobAppId.isNullOrEmpty() &&
        !releaseAdMobBannerUnitId.isNullOrEmpty() &&
        !releaseAdMobInterstitialUnitId.isNullOrEmpty()
// SMCPKG_SUPPORT<<<Cursor031

// SMCPKG_SUPPORT>>>Cursor009
/** Current release name. Next release: increment the third component by 1 (1.0.0 → 1.0.1). */
// SMCPKG_SUPPORT>>>Cursor011
// val APP_VERSION_NAME = "1.0.0"
// val APP_VERSION_NAME = "1.0.1"
// val APP_VERSION_NAME = "1.0.2"
// val APP_VERSION_NAME = "1.0.3"
// val APP_VERSION_NAME = "1.0.4"
// val APP_VERSION_NAME = "1.0.5"
// val APP_VERSION_NAME = "1.0.6"
// val APP_VERSION_NAME = "1.0.7"
// val APP_VERSION_NAME = "1.0.8"
// val APP_VERSION_NAME = "1.0.9"
// val APP_VERSION_NAME = "1.0.10"
// val APP_VERSION_NAME = "1.0.11"
// val APP_VERSION_NAME = "1.0.12"
// SMCPKG_SUPPORT>>>Cursor024
// val APP_VERSION_NAME = "1.0.13"
// SMCPKG_SUPPORT>>>Cursor030
// val APP_VERSION_NAME = "1.0.14"
// val APP_VERSION_NAME = "1.0.0"
val APP_VERSION_NAME = "1.0.1"
// SMCPKG_SUPPORT<<<Cursor030
// SMCPKG_SUPPORT<<<Cursor024
// SMCPKG_SUPPORT<<<Cursor011

/**
 * Maps versionName to versionCode: 1.0.0 → 100 … 1.0.14 → 114.
 * Increment versionCode by 1 whenever versionName increases by 0.01.
 */
fun versionCodeFor(versionName: String): Int {
    val parts = versionName.split(".")
    require(parts.size == 3) { "versionName must be major.minor.patch, e.g. 1.0.0" }
    val major = parts[0].toInt()
    val minor = parts[1].toInt()
    val patch = parts[2].toInt()
    return major * 100 + minor * 0 + patch
}
// SMCPKG_SUPPORT<<<Cursor009

android {
    // SMCPKG_SUPPORT>>>Cursor021
    // namespace = "com.example.quadvideoplayer"
    // SMCPKG_SUPPORT>>>Cursor030
    // namespace = "com.apsmkimo.tetraview"
    namespace = "com.apsmkimo.tetravideoplayer"
    // SMCPKG_SUPPORT<<<Cursor030
    // SMCPKG_SUPPORT<<<Cursor021
    // SMCPKG_SUPPORT>>>Cursor001
    // compileSdk = 35
    compileSdk = 36
    // SMCPKG_SUPPORT<<<Cursor001

    defaultConfig {
        // SMCPKG_SUPPORT>>>Cursor021
        // applicationId = "com.example.quadvideoplayer"
        // SMCPKG_SUPPORT>>>Cursor030
        // applicationId = "com.apsmkimo.tetraview"
        applicationId = "com.apsmkimo.tetravideoplayer"
        // SMCPKG_SUPPORT<<<Cursor030
        // SMCPKG_SUPPORT<<<Cursor021
        minSdk = 24
        targetSdk = 35
        // SMCPKG_SUPPORT>>>Cursor009
        // versionCode = 1
        // versionName = "1.0"
        //
        // Release versioning:
        // - versionName starts at "1.0.0".
        // - Each subsequent release increments versionName by 0.01
        //   (next = 1.0.1, then 1.0.2, …).
        // - versionCode is a simple integer: 100 for 1.0.0, then +1 per release
        //   (1.0.1 → 101, 1.0.2 → 102). Helper below keeps them in sync.
        versionName = APP_VERSION_NAME
        versionCode = versionCodeFor(APP_VERSION_NAME)
        // SMCPKG_SUPPORT<<<Cursor009
        // SMCPKG_SUPPORT>>>Cursor031
        applyAdMobIds(
            TEST_ADMOB_APP_ID,
            TEST_ADMOB_BANNER_UNIT_ID,
            TEST_ADMOB_INTERSTITIAL_UNIT_ID,
        )
        // SMCPKG_SUPPORT<<<Cursor031
    }

    // SMCPKG_SUPPORT>>>Cursor012
    // Debug APKs (local + GitHub Actions assembleDebug) always use the committed
    // TetraVideoPlayerAD-only keystore at app/debug.keystore. This material is
    // not shared with FreeQuadPlayer / TetraView, so the two apps cannot
    // overwrite each other on a device.
    signingConfigs {
        getByName("debug") {
            storeFile = file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }
    // SMCPKG_SUPPORT<<<Cursor012

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
            // SMCPKG_SUPPORT>>>Cursor031
            // Debug always ships Google test IDs, even if local.properties has
            // production overrides. That keeps local installs off live inventory.
            applyAdMobIds(
                TEST_ADMOB_APP_ID,
                TEST_ADMOB_BANNER_UNIT_ID,
                TEST_ADMOB_INTERSTITIAL_UNIT_ID,
            )
            // SMCPKG_SUPPORT<<<Cursor031
        }
        release {
            // No dedicated release keystore is committed. Do not fall back to
            // FreeQuadPlayer / TetraView signing material.
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            // SMCPKG_SUPPORT>>>Cursor031
            // Configuration-time placeholders stay on test IDs so IDE sync and
            // assembleDebug still work without secrets. assembleRelease / bundleRelease
            // fail later if production IDs are missing (see gradle.taskGraph.whenReady).
            if (hasReleaseAdMobIds) {
                applyAdMobIds(
                    releaseAdMobAppId!!,
                    releaseAdMobBannerUnitId!!,
                    releaseAdMobInterstitialUnitId!!,
                )
            } else {
                applyAdMobIds(
                    TEST_ADMOB_APP_ID,
                    TEST_ADMOB_BANNER_UNIT_ID,
                    TEST_ADMOB_INTERSTITIAL_UNIT_ID,
                )
            }
            // SMCPKG_SUPPORT<<<Cursor031
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // SMCPKG_SUPPORT>>>Cursor003
    // kotlinOptions {
    //     jvmTarget = "17"
    // }
    // SMCPKG_SUPPORT<<<Cursor003

    buildFeatures {
        compose = true
        // SMCPKG_SUPPORT>>>Cursor031
        buildConfig = true
        // SMCPKG_SUPPORT<<<Cursor031
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
        // SMCPKG_SUPPORT>>>Cursor009
        jniLibs {
            // SMCPKG_SUPPORT>>>Cursor030
            // Ship uncompressed libffmpegJNI.so and LGPL FFmpeg shared .so from :decoder-ffmpeg.
            // SMCPKG_SUPPORT<<<Cursor030
            useLegacyPackaging = false
        }
        // SMCPKG_SUPPORT<<<Cursor009
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.common)
    // SMCPKG_SUPPORT>>>Cursor009
    // Official androidx.media3:media3-decoder-ffmpeg:1.11.0 is not on Maven.
    // Local module vendors Media3 1.11.0 decoder_ffmpeg + prebuilt FFmpeg JNI.
    implementation(project(":decoder-ffmpeg"))
    // SMCPKG_SUPPORT<<<Cursor009

    // SMCPKG_SUPPORT>>>Cursor005
    implementation(libs.coil.compose)
    implementation(libs.coil.video)
    // SMCPKG_SUPPORT<<<Cursor005

    implementation(libs.play.services.ads)
}

// SMCPKG_SUPPORT>>>Cursor031
gradle.taskGraph.whenReady {
    val requestedRelease = allTasks.any { task ->
        Regex("^(assemble|bundle|publish).*Release.*").matches(task.name)
    }
    if (requestedRelease && !hasReleaseAdMobIds) {
        throw GradleException(
            "Release builds require ADMOB_APP_ID, ADMOB_BANNER_UNIT_ID, and " +
                "ADMOB_INTERSTITIAL_UNIT_ID in local.properties or the environment. " +
                "See README.md. Debug builds always use Google official test IDs.",
        )
    }
}
// SMCPKG_SUPPORT<<<Cursor031
