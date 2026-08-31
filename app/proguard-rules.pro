# ==============================================================================
# SnapData Production ProGuard / R8 Optimization & Obfuscation Rules
# ==============================================================================

# Keep Core KotlinX Serialization
-keepattributes *Annotation*, InnerClasses, EnclosingMethod
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
-keep class kotlinx.serialization.json.** { *; }
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

# SnapData Data Models, Enums & Entities
-keep class com.example.snapdata.model.** { *; }
-keep class com.example.snapdata.data.** { *; }
-keepclassmembers class com.example.snapdata.data.DocumentEntity { *; }
-keepclassmembers enum com.example.snapdata.model.** { *; }

# AndroidX Room Database & SQLite DAOs
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase { *; }
-dontwarn androidx.room.paging.**

# Google ML Kit Vision & Text Recognition
-keep class com.google.mlkit.vision.text.** { *; }
-keep class com.google.android.gms.vision.** { *; }
-keep class com.google.android.gms.internal.mlkit_vision_text_common.** { *; }
-keep class com.google.android.gms.tasks.** { *; }
-dontwarn com.google.mlkit.**
-dontwarn com.google.android.gms.**

# OkHttp3 & Okio Network Client
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
-keepattributes Signature
-keepattributes *Annotation*
-keepclassmembers class * extends okhttp3.Response { *; }
-keepclassmembers class * extends okhttp3.Request { *; }

# Coil Image Loader
-keep class coil.** { *; }
-dontwarn coil.**

# Jetpack Compose & Lifecycle Runtime
-keep class androidx.compose.** { *; }
-keep class androidx.lifecycle.** { *; }

# Firebase Authentication
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**
-keep class com.google.firebase.auth.FirebaseAuth { *; }
-keep class com.google.firebase.auth.FirebaseUser { *; }
-keepclassmembers class com.google.firebase.auth.** { *; }

# EncryptedSharedPreferences / Jetpack Security Crypto
-keep class androidx.security.crypto.** { *; }
-dontwarn androidx.security.crypto.**

# SnapData Auth Layer
-keep class com.example.snapdata.auth.** { *; }

