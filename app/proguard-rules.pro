# SMCPKG_SUPPORT>>>Cursor039
# Release minify is on (R8). Keep line numbers so Play can use mapping.txt.
# QuadVideoPlayer — debug/release without minify for the sample app.
# Keep Media3 / Compose defaults if minify is later enabled.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
# SMCPKG_SUPPORT<<<Cursor039

# SMCPKG_SUPPORT>>>Cursor009
# Media3 FFmpeg extension (loaded by DefaultRenderersFactory via reflection).
-keep class androidx.media3.decoder.ffmpeg.** { *; }
-keep class androidx.media3.decoder.VideoDecoderOutputBuffer { *; }
-keepclasseswithmembernames class * {
    native <methods>;
}
# SMCPKG_SUPPORT<<<Cursor009
