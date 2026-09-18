# Third-party notices

TetraVideoPlayer is proprietary software (All rights reserved). The following
components keep their original licenses.

## AndroidX / Jetpack Compose / Media3

Apache License 2.0  
https://www.apache.org/licenses/LICENSE-2.0

Official `androidx.media3:media3-decoder-ffmpeg` is not published on Maven.
`decoder-ffmpeg/` vendors Media3 1.11.0 `decoder_ffmpeg` Java/JNI sources
(Apache 2.0).

## Coil

Apache License 2.0  
https://github.com/coil-kt/coil

## Google Mobile Ads SDK (Google Play services)

Google Play services / Google Mobile Ads SDK is used to show Google-provided
test banners and a test interstitial on the layout picker.

Copyright Google LLC. Use is subject to the Google APIs Terms of Service and
the Google Mobile Ads SDK terms:

https://developers.google.com/admob  
https://developers.google.com/admob/android/quick-start

Debug and other public/default builds use Google official test application
and ad unit IDs only. Release builds may inject production IDs at compile
time from gitignored `local.properties` or environment variables; those
values are not committed. See README.md.

## FFmpeg 6.0 (LGPL-only, dynamically linked)

FFmpeg is used under **LGPL 2.1 or later**.

Configure is LGPL-only:

- `--enable-shared --disable-static`
- `--disable-gpl`
- `--disable-nonfree`
- no `--enable-gpl`
- no `--enable-nonfree`
- `--disable-postproc` (postproc is GPL)
- `--disable-everything` plus an allowlisted set of built-in decoders

`libffmpegJNI.so` dynamically links `libavcodec.so`, `libavutil.so`,
`libswscale.so`, and `libswresample.so`. Those shared objects are packaged
next to the JNI wrapper so FFmpeg can be replaced independently.

LGPL text: https://www.gnu.org/licenses/old-licenses/lgpl-2.1.html  
FFmpeg: https://ffmpeg.org/

Native rebuild: `./decoder-ffmpeg/rebuild-native.sh`
