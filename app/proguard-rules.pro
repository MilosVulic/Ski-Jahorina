# Crashlytics-readable stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# Kotlin / coroutines
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-dontwarn kotlinx.coroutines.**

# Parcelize / Parcelable
-keep class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Firestore & Firebase model classes
-keep enum com.neoapps.skijahorina.** { *; }
-keep class com.neoapps.skijahorina.features.skicenter.properties.Property { *; }
-keep class com.neoapps.skijahorina.features.skicenter.camera.Camera { *; }
-keep class com.neoapps.skijahorina.common.PreferenceProvider { *; }
-keep class com.neoapps.skijahorina.common.AppAnalytics { *; }

-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Retrofit
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**

# Gson (Retrofit converter)
-keep class com.google.gson.** { *; }
-keep class com.neoapps.skijahorina.features.skicenter.data.api.** { *; }
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule { <init>(...); }
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

# Google Ads
-keep class com.google.android.gms.ads.** { *; }

# ViewBinding
-keep class com.neoapps.skijahorina.databinding.** { *; }

# Navigation Safe Args
-keepnames class androidx.navigation.fragment.NavArgsLazy

# Jsoup
-dontwarn org.jsoup.**

# Media3 / ExoPlayer
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# BuildConfig
-keep class com.neoapps.skijahorina.BuildConfig { *; }
