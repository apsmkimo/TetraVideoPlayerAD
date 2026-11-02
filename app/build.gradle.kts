import java.util.Properties
import com.android.build.api.artifact.MultipleArtifact
import com.android.build.api.artifact.SingleArtifact
import java.io.File
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.bundling.Zip
import org.gradle.process.ExecOperations

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // Renders the layout menu to a PNG without an emulator (recordPaparazziDebug).
    // id("app.cash.paparazzi") version "2.0.0-alpha05"
    // Paparazzi 2.0.0-alpha05 needs JVM 21. The Debug APK job runs assembleDebug
    // on JDK 17, and applying the plugin fails configuration before compilation.
    // The recorded menu PNG stays in app/src/test/snapshots.
}

/** Current release name. Next release: increment the third component by 1 (1.0.0 → 1.0.1). */
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
// val APP_VERSION_NAME = "1.0.13"
// val APP_VERSION_NAME = "1.0.14"
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
val APP_VERSION_NAME = "1.0.12"

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

// Play Console upload-key signing. Values are read in this order:
// 1) environment variables (GitHub Actions)
// 2) -P Gradle properties
// 3) local.properties (gitignored; local machine only)
// Missing values leave the release buildType unsigned so debug CI never fails.
val localProperties = Properties().apply {
    val localFile = rootProject.file("local.properties")
    if (localFile.exists()) {
        localFile.inputStream().use(::load)
    }
}

fun releaseSigningValue(name: String): String? {
    sequenceOf(
        System.getenv(name),
        project.findProperty(name) as String?,
        localProperties.getProperty(name),
    ).forEach { raw ->
        val trimmed = raw?.trim().orEmpty()
        if (trimmed.isNotEmpty()) return trimmed
    }
    return null
}

val releaseKeystoreFile = releaseSigningValue("KEYSTORE_FILE")
val releaseKeystorePassword = releaseSigningValue("KEYSTORE_PASSWORD")
val releaseKeyAlias = releaseSigningValue("KEY_ALIAS")
val releaseKeyPassword = releaseSigningValue("KEY_PASSWORD")
val hasReleaseSigning =
    !releaseKeystoreFile.isNullOrEmpty() &&
        !releaseKeystorePassword.isNullOrEmpty() &&
        !releaseKeyAlias.isNullOrEmpty() &&
        !releaseKeyPassword.isNullOrEmpty()

android {
    // namespace = "com.example.quadvideoplayer"
    // namespace = "com.apsmkimo.tetraview"
    namespace = "com.apsmkimo.tetravideoplayer"
    // compileSdk = 35
    compileSdk = 36
    // AGP 8.7 default. Required so release can extract native debug symbols
    // from the prebuilt FFmpeg .so files (Play Console native-symbols warning).
    ndkVersion = "27.0.12077973"

    defaultConfig {
        // applicationId = "com.example.quadvideoplayer"
        // applicationId = "com.apsmkimo.tetraview"
        applicationId = "com.apsmkimo.tetravideoplayer"
        minSdk = 24
        // targetSdk = 35
        // Play Console (2026) requires target API 36 for new uploads.
        targetSdk = 36
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
    }

    // Debug APKs (local + GitHub Actions assembleDebug) always use the committed
    // TetraVideoPlayerAD-only keystore at app/debug.keystore. This material is
    // not shared with FreeQuadPlayer / TetraView, so the two apps cannot
    // overwrite each other on a device.
    // signingConfigs {
    //     getByName("debug") {
    //         storeFile = file("debug.keystore")
    //         storePassword = "android"
    //         keyAlias = "androiddebugkey"
    //         keyPassword = "android"
    //     }
    // }
    signingConfigs {
        getByName("debug") {
            storeFile = file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
        if (hasReleaseSigning) {
            create("release") {
                storeFile = file(releaseKeystoreFile!!)
                storePassword = releaseKeystorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            // // No dedicated release keystore is committed. Do not fall back to
            // // FreeQuadPlayer / TetraView signing material.
            // Apply upload-key signing only when KEYSTORE_* credentials are present.
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
            // isMinifyEnabled = false
            // R8 writes mapping.txt and AGP 4.1+ embeds it in the AAB
            // (BUNDLE-METADATA/com.android.tools.build.obfuscation/proguard.map).
            isMinifyEnabled = true
            isShrinkResources = true
            // SYMBOL_TABLE: committed libffmpegJNI.so files are already stripped
            // (no DWARF). This still packages the dynamic symbol table into the
            // AAB so Play can symbolicate native crashes. FULL would not add
            // file/line info for these binaries.
            ndk {
                debugSymbolLevel = "SYMBOL_TABLE"
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // kotlinOptions {
    //     jvmTarget = "17"
    // }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
        jniLibs {
            // Ship uncompressed libffmpegJNI.so and LGPL FFmpeg shared .so from :decoder-ffmpeg.
            useLegacyPackaging = false
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

// Prebuilt libffmpegJNI.so / AndroidX .so files are already stripped, so AGP's
// extractReleaseNativeSymbolTables skips them and Play Console reports missing
// native debug symbols. Append .sym files (dynamic symbol table) for every
// merged native library. AGP packages **/*.sym into the AAB.
abstract class NativeSymbolTableTask : DefaultTask() {
    @get:Inject
    abstract val execOperations: ExecOperations

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val mergedNativeLibs: DirectoryProperty

    @get:Input
    abstract val ndkVersionName: Property<String>

    @get:Input
    abstract val sdkDir: Property<String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
        val outRoot = outputDirectory.get().asFile
        outRoot.deleteRecursively()
        val merged = mergedNativeLibs.get().asFile
        val libRoot = merged.resolve("lib").takeIf { it.isDirectory } ?: merged
        val objcopy = findLlvmObjcopy(sdkDir.get(), ndkVersionName.get())
        libRoot.listFiles()?.filter { it.isDirectory }?.forEach { abiDir ->
            abiDir.listFiles()?.filter { it.isFile && it.name.endsWith(".so") }?.forEach { so ->
                val dest = outRoot.resolve(abiDir.name).resolve("${so.name}.sym")
                dest.parentFile.mkdirs()
                if (objcopy != null) {
                    execOperations.exec {
                        commandLine(objcopy.absolutePath, "--strip-debug", so.absolutePath, dest.absolutePath)
                        isIgnoreExitValue = true
                    }
                }
                if (!dest.isFile || dest.length() == 0L) {
                    so.copyTo(dest, overwrite = true)
                }
            }
        }
    }

    private fun findLlvmObjcopy(sdkDirPath: String, ndkVersionName: String): File? {
        if (sdkDirPath.isBlank()) return null
        val sdkDir = File(sdkDirPath)
        val prebuilt = sdkDir.resolve("ndk/$ndkVersionName/toolchains/llvm/prebuilt")
        val host = prebuilt.listFiles()?.firstOrNull { it.isDirectory } ?: return null
        val objcopy = host.resolve("bin/llvm-objcopy")
        return objcopy.takeIf { it.canExecute() }
    }
}

androidComponents {
    onVariants(selector().withBuildType("release")) { variant ->
        val symbolTask = tasks.register<NativeSymbolTableTask>(
            "generate${variant.name.replaceFirstChar { it.uppercase() }}NativeSymbolTables",
        ) {
            ndkVersionName.set("27.0.12077973")
            sdkDir.set(
                providers.provider {
                    localProperties.getProperty("sdk.dir")
                        ?: System.getenv("ANDROID_SDK_ROOT")
                        ?: System.getenv("ANDROID_HOME")
                        ?: ""
                },
            )
            mergedNativeLibs.set(variant.artifacts.get(SingleArtifact.MERGED_NATIVE_LIBS))
        }
        variant.artifacts.use(symbolTask)
            .wiredWith { it.outputDirectory }
            .toAppendTo(MultipleArtifact.NATIVE_SYMBOL_TABLES)
        tasks.register<Zip>("zip${variant.name.replaceFirstChar { it.uppercase() }}NativeDebugSymbols") {
            dependsOn(symbolTask)
            from(symbolTask.flatMap { it.outputDirectory })
            archiveFileName.set("native-debug-symbols.zip")
            destinationDirectory.set(layout.buildDirectory.dir("outputs/native-debug-symbols/${variant.name}"))
            // Stable CI path even when AGP only embeds symbols inside the AAB.
        }
    }
}

tasks.matching { it.name == "bundleRelease" }.configureEach {
    dependsOn("zipReleaseNativeDebugSymbols")
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
    // Official androidx.media3:media3-decoder-ffmpeg:1.11.0 is not on Maven.
    // Local module vendors Media3 1.11.0 decoder_ffmpeg + prebuilt FFmpeg JNI.
    implementation(project(":decoder-ffmpeg"))

    implementation(libs.coil.compose)
    implementation(libs.coil.video)

    implementation(libs.play.services.ads)
    implementation(libs.user.messaging.platform)
    implementation(libs.billing.ktx)

    // Play Console flags transitive androidx.fragment:fragment:1.1.0 (below 1.2.1).
    // A direct 1.8.9 dependency wins Gradle highest-version resolution.
    implementation(libs.androidx.fragment)
    // Per-app locales: AppCompatDelegate.setApplicationLocales and autoStoreLocales.
    implementation(libs.androidx.appcompat)
    testImplementation("junit:junit:4.13.2")
}
