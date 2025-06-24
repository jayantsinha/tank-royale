# ProGuard rules for Robocode Tank Royale Observer

# Keep main class
-keep public class dev.robocode.tankroyale.observer.ObserverAppKt {
    public static void main(java.lang.String[]);
}

# Keep all public API classes that might be accessed externally
-keep public class dev.robocode.tankroyale.observer.** {
    public *;
}

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# Keep Swing/AWT classes
-keep class javax.swing.** { *; }
-keep class java.awt.** { *; }

# Keep coroutines
-keep class kotlinx.coroutines.** { *; }

# Keep serialization classes
-keep class kotlinx.serialization.** { *; }
-keep @kotlinx.serialization.Serializable class * {
    static **[] values();
    static ** valueOf(java.lang.String);
    *;
}

# Keep WebSocket client classes
-keep class org.java_websocket.** { *; }

# Optimize code
-optimizationpasses 5
-allowaccessmodification
-mergeinterfacesaggressively

# Remove debug info
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
    static void checkNotNullParameter(java.lang.Object, java.lang.String);
}

# Don't warn about missing classes
-dontwarn org.slf4j.**
-dontwarn ch.qos.logback.**
