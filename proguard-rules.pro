-dontwarn
-dontwarn
-keep class dev.keiji.license.maven.gradle.MainKt {
    public static void main(java.lang.String[]);
}
-keep class org.jetbrains.kotlinx.cli.** { *; }
-keep class kotlinx.serialization.** { *; }
-keep class okhttp3.** { *; }
-keep class dev.keiji.license.maven.gradle.Processor {
    <methods>;
}
-keep class okio.** { *; }
