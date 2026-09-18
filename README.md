# TetraVideoPlayer - 4-Way Multi-Video Player

## Overview

TetraVideoPlayer is an Android app for watching up to four local videos at once. Each launch you pick one of four layouts: a 2×2 landscape grid, a 1×4 portrait stack, a 1×2 portrait stack, or a 2×1 landscape pair. Independent Media3 ExoPlayer instances compare, mix, and review device footage—with simultaneous audio, per-cell controls, and strong support for legacy formats such as older AVI files.

## Key Features

- **Layout picker every launch** — A 2×2 tile grid cropped from the reference artwork: **2x2 Grid**, **1x4 Stack**, **2x1 Horizontal**, **1x2 Vertical**. The choice is in-session only (not stored). Cold start always shows the picker. Tap the layout button in the player (top-right) to return to selection: all playback stops, media is released, and the next layout starts with empty cells. The info button opens **About** (proprietary license, repository URL, and third-party component licenses). AdMob banners sit in the empty top and bottom bands; the first layout pick in a process may show an interstitial. The top-right **Remove Ads** button is a Google Play Billing one-time purchase (`remove_ads`); after purchase or restore it disappears and banners/interstitials stay off. Debug builds can long-press **About** to show ads again for testing.
- **2x2 Grid** — Four independent local videos in a landscape 2×2 grid.
- **1x4 Stack** — Four players stacked top-to-bottom in portrait, hairline separators, FIT letterbox.
- **1x2 Vertical** — Two players stacked top-to-bottom in portrait.
- **2x1 Horizontal** — Two players side-by-side in landscape.
- **Intelligent Aspect-Ratio Matching** — Each cell uses FIT scaling so videos keep their native aspect ratio, with letterboxing or pillarboxing (black bars) when dimensions do not match the cell.
- **Folder-Grouped Video Picker** — Browse local videos by device directory (MediaStore buckets) with Coil thumbnails, file names, and durations instead of a flat system file browser.
- **Ultra-Thin Borders & Clean UI** — No title or action bar; cells maximize the viewing area with hairline separators only.
- **4-Way Independent Gesture Controls & Simultaneous Audio Mixing** — Single-tap a playing cell to show or hide the overlay toolbar (play/pause, seek, times, mute, folder). Press and vertical-drag on the video maps volume from the cell frame (top = max, bottom = mute); a HUD bar about 80% of screen height appears while dragging. All players can output audio together.
- **Built-in High Fault-Tolerance for Legacy Formats** — FFmpeg-prefer software decoding for common old AVI codecs, audio-driven sync, skip/drop of late frames, and generous per-player buffering for smoother legacy playback.

## Tech Stack

<!-- SMCPKG_SUPPORT>>>Cursor034 -->
<!-- Old CI/CD row: GitHub Actions (`./gradlew assembleDebug`, artifact `app-debug`) -->
<!-- SMCPKG_SUPPORT<<<Cursor034 -->

| Layer | Choice |
| --- | --- |
| Language | Kotlin |
| UI | Jetpack Compose (Material 3) |
| Playback | Jetpack Media3 ExoPlayer |
| Decode | Media3 FFmpeg Extension (FFmpeg software decoding) |
| Thumbnails | Coil (`coil-compose`, `coil-video`) |
| Billing | Google Play Billing Library (`remove_ads` one-time IAP) |
| CI/CD | GitHub Actions (`assembleDebug` → `app-debug`; `bundleRelease` → `app-release` AAB on `main` / `workflow_dispatch` when signing secrets are set) |

**Package ID:** `com.apsmkimo.tetravideoplayer`  
**SDK:** minSdk 24 · compileSdk 36 · targetSdk 35  
**Version:** 1.0.2 (`versionCode` 102)

Official `androidx.media3:media3-decoder-ffmpeg` is not published on Maven Central. TetraVideoPlayer vendors the Media3 1.11.0 `decoder_ffmpeg` module with a prebuilt `libffmpegJNI.so` plus LGPL-only shared FFmpeg libraries. Modern H.264/HEVC streams stay on hardware MediaCodec; FFmpeg is preferred for allowlisted legacy codecs. See [decoder-ffmpeg/README.md](decoder-ffmpeg/README.md) for native rebuild notes.

## License

TetraVideoPlayer is **proprietary** software. Copyright (C) 2026 apsmkimo. **All rights reserved.**  
The application as a whole is **not** licensed under GPLv3. See [LICENSE](LICENSE).

Third-party components keep their own licenses (Apache 2.0 for AndroidX / Media3 / Coil; **FFmpeg 6.0 LGPL-only**, built without `--enable-gpl` / `--enable-nonfree` and dynamically linked as shared `.so` libraries). See [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md).

## How to Download & Test

GitHub Actions builds a debug APK on every push to `main` and uploads it as an artifact:

1. Open this repository on GitHub: [apsmkimo/TetraVideoPlayerAD](https://github.com/apsmkimo/TetraVideoPlayerAD).
2. Select the **Actions** tab.
3. In the left sidebar, open the **Android CI** workflow.
4. Choose the latest run with a green check mark (successful build on `main`).
5. Scroll to the **Artifacts** section at the bottom of the run page.
6. Download **`app-debug`** (GitHub delivers a zip file).
7. Unzip the archive to get `app-debug.apk`.
8. Copy the APK to an Android device or emulator (minSdk 24).
9. Allow installation from that source if prompted, then open the APK to install TetraVideoPlayer.
10. Grant video-read permission when asked (`READ_MEDIA_VIDEO` on Android 13+, otherwise `READ_EXTERNAL_STORAGE`).
11. Every launch, tap one of **2x2 Grid**, **1x4 Stack**, **1x2 Vertical**, or **2x1 Horizontal**. Tap an empty cell, pick a folder, then a video. Tap a playing cell to show overlay controls. Use the top-right layout button to switch modes later.

### Build from source (optional)

```bash
./gradlew assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

GitHub Actions does not require the Android NDK; FFmpeg JNI libraries are prebuilt and committed. Rebuilding natives needs NDK r26b and FFmpeg 6.0 (`./decoder-ffmpeg/rebuild-native.sh`).

## Remove Ads (Play Billing)

The layout picker’s top-right **Remove Ads** control launches a one-time Google Play purchase. Product id (must match Play Console exactly):

```
remove_ads
```

Create it in Play Console as a **managed / one-time** in-app product (not a subscription, not consumable), activate it, and ship a build that includes this Billing Library integration (an internal testing track is enough). Add license testers under **Setup → License testing** to buy without charge. When BillingClient is ready the app queries existing INAPP purchases so a reinstall restores entitlement. The button is hidden after purchase or restore. If Play Billing is missing or the product is not available, the app shows a short toast and leaves ads on.

## Debug signing (overwrite installs)

Local `./gradlew assembleDebug` and GitHub Actions CI use the same committed **TetraVideoPlayerAD-only** debug keystore so a new debug APK can overwrite an older TetraVideoPlayer debug install without uninstalling. This keystore is **not** the FreeQuadPlayer / TetraView debug key — the two apps have different `applicationId` values and different certificates, so neither can overwrite the other on a device.

- Keystore: [`app/debug.keystore`](app/debug.keystore)
- Alias: `androiddebugkey`
- Store / key password: `android`

`app/build.gradle.kts` `signingConfigs.debug` points at that file. `*.keystore` stays in `.gitignore` except this one (`!app/debug.keystore`). If you still see a signing mismatch, the device has an older APK signed with a different key—uninstall once, then future upgrades from this keystore will succeed.

<!-- SMCPKG_SUPPORT>>>Cursor034 -->
## Release signing & Play Console AAB

GitHub Actions on `main` (and **Actions → Android CI → Run workflow**) can produce a **signed `app-release.aab`** for Play Console. The keystore never lives in git: CI decodes it from GitHub Actions secrets onto the runner, then Gradle reads `KEYSTORE_FILE`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, and `KEY_PASSWORD`. Debug APK jobs do not need these secrets.

With [Play App Signing](https://support.google.com/googleplay/android-developer/answer/9842756), this keystore is the **upload key**. Google re-signs the app with the app signing key. Keep the upload JKS and passwords offline as well as in secrets; losing the upload key requires a Play Console reset.

### 1. Create an upload keystore (once)

<!-- SMCPKG_SUPPORT>>>Cursor034
Old example used upload-keystore.jks / alias upload.
SMCPKG_SUPPORT<<<Cursor034 -->

```bash
keytool -genkeypair -v \
  -storetype JKS \
  -keystore tetra-upload.jks \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -alias tetraupload
```

Use a strong store password (and key password if prompted). Do not commit `tetra-upload.jks`.

### 2. Base64 the keystore

Linux:

```bash
base64 -w 0 tetra-upload.jks
```

macOS:

```bash
base64 -i tetra-upload.jks
```

Copy the single-line output. Do not commit the base64 string.

### 3. Set GitHub Actions secrets

Repo → **Settings → Secrets and variables → Actions → New repository secret**, or `gh secret set`. Create exactly these four names (values never belong in git):

| Secret | Value |
| --- | --- |
| `ANDROID_KEYSTORE_BASE64` | Base64 of `tetra-upload.jks` |
| `ANDROID_KEYSTORE_PASSWORD` | Keystore (store) password |
| `ANDROID_KEY_ALIAS` | `tetraupload` |
| `ANDROID_KEY_PASSWORD` | Key password |

```bash
# Values come from your offline JKS / secrets.txt — do not commit them.
gh secret set ANDROID_KEYSTORE_BASE64 --repo apsmkimo/TetraVideoPlayerAD < tetra-upload.jks.b64
printf '%s' "$ANDROID_KEYSTORE_PASSWORD" | gh secret set ANDROID_KEYSTORE_PASSWORD --repo apsmkimo/TetraVideoPlayerAD
printf '%s' "tetraupload" | gh secret set ANDROID_KEY_ALIAS --repo apsmkimo/TetraVideoPlayerAD
printf '%s' "$ANDROID_KEY_PASSWORD" | gh secret set ANDROID_KEY_PASSWORD --repo apsmkimo/TetraVideoPlayerAD
```

PRs and forks skip the release job (or skip the Gradle steps when any secret is empty), so they stay green without these secrets.

### 4. Download the AAB from Actions

1. After a successful run on `main` (or a manual **Run workflow**), open **Actions → Android CI**.
2. Download the **`app-release`** artifact (zip).
3. Unzip to get `app-release.aab` (`app/build/outputs/bundle/release/` on the runner).

Sideload testing still uses the **`app-debug`** APK artifact from the same workflow.

### 5. Upload to Play Console (internal testing)

1. Open [Play Console](https://play.google.com/console) → the `com.apsmkimo.tetravideoplayer` app.
2. Enroll in **Play App Signing** if the first upload asks for it. Register this JKS as the **upload key**.
3. **Testing → Internal testing → Create new release**.
4. Upload `app-release.aab`, save, and roll out to internal testers.

### Local `bundleRelease` (optional)

`local.properties` is gitignored. You can point Gradle at a local upload keystore:

```
KEYSTORE_FILE=/absolute/path/to/tetra-upload.jks
KEYSTORE_PASSWORD=...
KEY_ALIAS=tetraupload
KEY_PASSWORD=...
```

Then:

```bash
./gradlew bundleRelease
```

Output: `app/build/outputs/bundle/release/app-release.aab`. The same four names also work as environment variables.
<!-- SMCPKG_SUPPORT<<<Cursor034 -->
