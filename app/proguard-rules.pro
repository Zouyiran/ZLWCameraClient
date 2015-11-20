# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Google\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:

-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

-keep public class io.vov.vitamio.MediaPlayer { *; }
-keep public class io.vov.vitamio.IMediaScannerService { *; }
-keep public class io.vov.vitamio.MediaScanner { *; }
-keep public class io.vov.vitamio.MediaScannerClient { *; }
-keep public class io.vov.vitamio.VitamioLicense { *; }
-keep public class io.vov.vitamio.Vitamio { *; }
-keep public class io.vov.vitamio.MediaMetadataRetriever { *; }

-dontwarn com.handmark.pulltorefresh.library.**
-keep class com.handmark.pulltorefresh.library.** { *;}
-dontwarn com.handmark.pulltorefresh.library.extras.**
-keep class com.handmark.pulltorefresh.library.extras.** { *;}
-dontwarn com.handmark.pulltorefresh.library.internal.**
-keep class com.handmark.pulltorefresh.library.internal.** { *;}

-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }

-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }

-keep class com.ctcc.zlwcamera.data_struct.**{*;}

-assumenosideeffects class com.ctcc.zlwcamera.tools.LogUtil{
    public static *** v(...);
    public static *** i(...);
    public static *** d(...);
    public static *** w(...);
    public static *** e(...);
}
