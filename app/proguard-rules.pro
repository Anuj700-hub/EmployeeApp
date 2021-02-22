# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/manas/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**
-dontshrink
-dontoptimize
-dontpreverify

-keepattributes *Annotation*
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}
# OrmLite uses reflection
-keep class com.j256.**
-keepclassmembers class com.j256.** { *; }
-keep enum com.j256.**
-keepclassmembers enum com.j256.** { *; }
-keep interface com.j256.**
-keepclassmembers interface com.j256.** { *; }
# Keep the helper class and its constructor
-keep class * extends com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
-keepclassmembers class * extends com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper {
  public <init>(android.content.Context);
}


-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends io.realm.RealmObject
-keep public class * implements java.io.Serializable

-dontwarn com.squareup.okhttp.**

# Keep the annotations
-keepattributes *Annotation*

# Keep all model classes that are used by OrmLite
# Also keep their field names and the constructor
-keep @com.j256.ormlite.table.DatabaseTable class * {
    @com.j256.ormlite.field.DatabaseField <fields>;
    @com.j256.ormlite.field.ForeignCollectionField <fields>;
    <init>();
}
-keep class com.hungerbox.customer.order.activity.GlobalActivity{public protected private *; }
-keep public class com.hungerbox.customer.config.** {
    *;
}
-keep public class com.hungerbox.customer.model.** {
  *;
}

-keep public class com.hungerbox.customer.offline.modelOffline.** {
 *;
}

-keep public class com.hungerbox.customer.event.** {
*;
}

-keep class in.juspay.** {*;}

-keepattributes SourceFile,LineNumberTable

-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

-keep class com.phonepe.** {*;}

-dontwarn javax.annotation.**
-ignorewarnings
-dontwarn com.clevertap.android.sdk.**
-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

-keep class org.apache.http.** { *; }
-dontwarn org.apache.http.**

-keep class com.squareup.okhttp.* { *; }
-dontwarn com.squareup.okhttp.**
-dontwarn okio.**

-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

-keep class retrofit2.** { *; }
-dontwarn retrofit2.**

-keepattributes Exceptions, Signature, InnerClasses, LineNumberTable
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}