# Keep Google Mobile Ads classes
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.ads.** { *; }

# Keep RevenueCat
-keep class com.revenuecat.purchases.** { *; }

# Keep Kotlinx serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep Coil
-keep class coil3.** { *; }

# Keep TensorFlow Lite
-keep class org.tensorflow.lite.** { *; }
