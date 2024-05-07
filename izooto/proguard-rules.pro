# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-ignorewarnings
-keepattributes LineNumberTable,SourceFile
-renamesourcefileattribute SourceFile
-keep public class com.momagic.DATB{*;}
-keep public class com.momagic.DATB$Builder{*;}
-keep public class com.momagic.PushTemplate{*;}
-keep public class com.momagic.PreferenceUtil{*;}
-keep public class com.momagic.AppConstant{*;}
-keep public class com.momagic.NotificationWebViewListener{*;}
-keep public class com.momagic.NotificationHelperListener{*;}
-keep public class com.momagic.TokenReceivedListener{*;}
-keep public class com.momagic.Payload{*;}
-repackageclasses 'com.momagic'
-useuniqueclassmembernames

