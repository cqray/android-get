# 保留R下面的资源
-keep class **.R$* {*;}
# 保留四大组件，自定义的Application等这些类不被混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class * extends androidx.fragment.app.Fragment
-keepnames public class * extends androidx.fragment.app.Fragment

# 保证AndroidViewModel构造函数不被混淆
-keepclassmembers class * extends androidx.lifecycle.AndroidViewModel {
    public <init>(android.app.Application);
}
# 保证LifecycleViewModel构造函数不被混淆
-keepclassmembers class * extends cn.cqray.android.lifecycle.LifecycleViewModel {
    public <init>(androidx.lifecycle.LifecycleOwner);
}

#保持全部实现 Serializable 接口的类成员
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
    <fields>;
}

# SmartRefreshLayout保留指定字段，因为代码中用到了反射
-keepclassmembers class com.scwang.smart.refresh.layout.SmartRefreshLayout {
    protected boolean mEnableLoadMore;
    protected boolean mEnableRefresh;
    protected boolean mEnableOverScrollDrag;
    protected boolean mManualLoadMore;
}

# ButterKnife混淆代码
-keep class butterknife.* { *; }
-keep class butterknife.internal.* { *; }
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
-dontwarn butterknife.internal.**

##---------------开始：Gson的proguard配置----------
-keepattributes Signature
-keepattributes *Annotation*
# Gson specific classes
-dontwarn sun.misc.**
-keep class com.google.gson.stream.** { *; }
#防止proguard从TypeAdapter、TypeAdapterFactory、，
#JsonSerializer、JsonDeserializer实例（因此可以在@JsonAdapter中使用）
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
# 防止R8将数据对象成员始终保留为null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
# 在R8版本3.0及更高版本中保留TypeToken及其子类的通用签名。
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken
##---------------结束：Gson的proguard配置----------


## TabLayout框架所有类不混淆
-keep class com.flyco.tablayout.** {*;}
## SmartRefreshLayout保留指定字段，因为代码中用到了反射
#-keepclassmembers class com.flyco.tablayout.CommonTabLayout {
#    protected ArrayList<CustomTabEntity> mTabEntitys;
##    protected boolean mEnableRefresh;
##    protected boolean mEnableOverScrollDrag;
##    protected boolean mManualLoadMore;
#}