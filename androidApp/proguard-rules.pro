-dontwarn org.slf4j.impl.StaticLoggerBinder

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes LineNumberTable
-keepattributes Signature, InnerClasses, EnclosingMethod

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# Firebase Crashlytics
-keepattributes *Annotation*
-keepclassmembers class ** {
    @com.google.firebase.crashlytics.* <fields>;
    @com.google.firebase.crashlytics.* <methods>;
}
-keepclassmembers class **.R$* {
    <fields>;
}
-keep class com.google.firebase.crashlytics.** { *; }
-keep class com.google.firebase.analytics.** { *; }

# Keep classes accessed via reflection from Compose Material3 bottom sheets
-keep class androidx.compose.material3.SheetState { *; }
-keep class androidx.compose.material3.internal.** { *; }
-dontwarn androidx.compose.material3.internal.**

# 1) Keep the core resource‐descriptor classes
-keep class dev.icerock.moko.resources.desc.** { *; }

-keep class pl.cuyer.rusthub.SharedRes$images { *; }

-dontwarn okhttp3.internal.Util

# Keep Compose indirect input nodes – Room checks for them via reflection at runtime
-keep class androidx.compose.ui.input.indirect.** { *; }