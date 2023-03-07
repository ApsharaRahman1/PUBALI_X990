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
-keep class * extends com.raizlabs.android.dbflow.config.DatabaseHolder { *; }


#------------------------初始设置项------------------------
# 指定代码的压缩级别 0 - 7(指定代码进行迭代优化的次数，在Android里面默认是5，这条指令也只有在可以优化时起作用。)
-optimizationpasses 5
# 混淆时不会产生形形色色的类名(混淆时不使用大小写混合类名)
-dontusemixedcaseclassnames
# 指定不去忽略非公共的库类(不跳过library中的非public的类)
-dontskipnonpubliclibraryclasses
# 指定不去忽略包可见的库类的成员
-dontskipnonpubliclibraryclassmembers
#不进行优化，建议使用此选项
-dontoptimize
 # 不进行预校验,Android不需要,可加快混淆速度
-dontpreverify

# 指定混淆时采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不改变
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
# 避免混淆泛型, 这在JSON实体映射时非常重要
-keepattributes Signature
# 抛出异常时保留代码行号
-keepattributes Exceptions,SourceFile,LineNumberTable


# 有了verbose这句话，混淆后就会生成映射文件
# 包含有类名->混淆后类名的映射关系
# 然后使用printmapping指定映射文件的名称
# 释放时候一定要注掉！！！
-verbose
-printmapping proguardMapping.txt
#-----------------------------------------------------------------------



# ---------------------------- Android原生相关 -----------------------------
# AndroidMainfest中所有的类不可进行混淆，自定义View默认也不会混淆
#继承activity,application,service,broadcastReceiver,contentprovider....不进行混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.support.multidex.MultiDexApplication
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
# 不混淆View中的setXxx()和getXxx()方法，以保证属性动画正常工作
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
   public <init>(android.content.Context);
   public <init>(android.content.Context, android.util.AttributeSet);
   public <init>(android.content.Context, android.util.AttributeSet, int);
}
#这个主要是在layout 中写的onclick方法android:onclick="onClick"，不进行混淆
#表示不混淆Activity中参数是View的方法，因为有这样一种用法，在XML中配置android:onClick=”buttonClick”属性，
#当用户点击该按钮时就会调用Activity中的buttonClick(View view)方法，如果这个方法被混淆的话就找不到了
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}

## 保留support下的所有类及其内部类
-keep class android.support.** {*;}
# 保留继承的
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**
# 对于R（资源）下的所有类及其方法，都不能被混淆
-keep class **.R* {*;}
-keep class **.R$* {*;}
# 保留R下面的资源
#-keep class **.R$* {
# *;
#}
#不混淆资源类下static的
-keepclassmembers class **.R$* {
    public static <fields>;
}


#------------------------枚举相关-------------------------
# 枚举类不能被混淆
-keepclassmembers enum * {*;}
#表示不混淆枚举中的values()和valueOf()方法，枚举我用的非常少，这个就不评论了
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
#-----------------------------------------------------------


#-----------序列化相关----------
# 保留Parcelable序列化的类不被混淆
-keep class * implements android.os.Parcelable {*;}
#毫无疑问，CREATOR字段是绝对不能改变的，包括大小写都不能变，不然整个Parcelable工作机制都会失败。
#表示不混淆Parcelable实现类中的CREATOR字段
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
# 保留Serializable序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {*;}
# 这指定了继承Serizalizable的类的如下成员不被移除混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
#----------------------------------------------------------


#-----------接口---------------
# 接口类不能被混淆
-keepclassmembers interface * {*;}
# 接口回调
# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}
#-------------------------------

#—-------------------Jni方法--------------------------
#表示不混淆任何包含native方法的类的类名以及native方法名，这个和我们刚才验证的结果是一致
-keepclasseswithmembernames class * {
    native <methods>;
}
#-----------------------------------------------------

#-------------------保留引用的jar包类--------------
-keep class android.serialport.SerialPort {*;}
-keep class com.socsi.** { *; }
-keep class com.ivsign.android.IDCReader.** { *; }
-keep class com.verifone.zxing.** {*;}
-keep class com.ivsign.android.IDCReader.** { *; }
-keep class socsi.middleware.** { *; }
-keep class socsi.serialport.** { *; }
-keep class com.laikey.jatools.** { *; }
-keep class com.verifone.smartpos.emvmiddleware.** {*;}
-keep class socsi.emvl1.** {*;}
-keep class org.bouncycastle.** {*;}
-keep class com.synodata.** {*;}
-keep class org.opencv.** {*;}
-keep class org.apache.commons.lang3.** {*;}
-keep class com.vfi.smartpos.deviceservice.aidl.**{*;}
-keep class cn.com.keshengxuanyi.mobilereader.** {*;}
-keep class com.synjones.bluetooth.** {*;}
-keep class com.verifyliceselib.** {*;}
-keep class net.sourceforge.zbar.** {*;}
-keep class com.verifone.decoder.** {*;}
-keep class org.bouncycastle.** {*;}
-keep class com.sygg.gson.** {*;}
-keep class com.synodata.** {*;}
-keep class org.sybouncycastle.** {*;}
-keep class org.syopencv.core.** {*;}
-keep class com.level2.** {*;}
#-------------------------------------------------

#---------verifone相关的jar-------
-ignorewarnings
-dontwarn com.socsi.**
-dontwarn socsi.middleware.**
-dontwarn org.bouncycastle.**
-dontwarn cn.com.keshengxuanyi.**
#----------------------------------

#-------------------第三方开源项目--------------------
##保留Bind标注
#-keep class * {
#    @butterknife.Bind <fields>;
#}
# Json
-keep class org.json.** {*;}
# okhttp3
-dontwarn okio.**
-dontwarn okhttp3.**
-dontwarn com.squareup.okhttp3.**
#Gson
-keep class com.google.gson.** {*;}
-keep class com.google.**{*;}
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }

# Rxjava RxAndroid
-dontwarn rx.*
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQuene*Field*{
long producerIndex;
long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
rx.internal.util.atomic.LinkedQueueNode producerNode;
rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
#-------------EventBus-----------------------
#-keepattributes *Annotation* 这句上边写过了
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
#------------dagger--------------------------
-keep class dagger.* {*;}
-keep class javax.inject.* {*;}
-keep class * extends dagger.internal.Binding
-keep class * extends dagger.internal.MouduleAdapter
-keep class * extends dagger.internal.StaticInjection

#DBFlow
#这句为DBflow 作者15年提供
-keep class com.raizlabs.android.dbflow.config.GeneratedDatabaseHolder
#使用者1的回复
-keep class com.raizlabs.android.dbflow.** { *; }
#使用者2的回复
-keep class * extends com.raizlabs.android.dbflow.config.DatabaseHolder { *; }
-keep class   com.raizlabs.android.dbflow.config.GeneratedDatabaseHolder
-keep class * extends com.raizlabs.android.dbflow.config.BaseDatabaseDefinition { *; }
#--------------------------------------------


# 保持测试相关的代码
-dontnote junit.framework.**
-dontnote junit.runner.**
-dontwarn android.test.**
-dontwarn android.support.test.**
-dontwarn org.junit.**


#-------------------------------------------颗粒细化部分----------------------------------
#-----------实体model(不能混淆，否则找不到对应的属性，获取不到值)-----
-keepattributes *Annotation*
-keep class * extends java.lang.annotation.Annotation { *; }

#-----------用到反射的类--------------------------------------------
-keepclassmembers class com.vfi.android.domain.entities.businessbeans.SwitchParameter { *; }
-keepclassmembers class com.vfi.android.domain.entities.businessbeans.TerminalCfg { *; }

#------------保持java.lang.invoke----------------------------
-dontwarn java.lang.invoke.**

