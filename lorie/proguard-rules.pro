-keepattributes SourceFile,LineNumberTable

# Keep classes/members referenced from JNI (FindClass/GetMethodID/GetFieldID/RegisterNatives) + main entrypoint
-keep class com.termux.x11.MainActivity {
    public <init>();
    void clientConnectedStateChanged();
    static native void nativeSetLaunchWindowingModeFreeform(java.lang.Object);
}

-keep class com.termux.x11.LorieView {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    private com.termux.x11.MainActivity activity;
    native <methods>;
    void resetIme();
    void requestClipboard();
    void onSyncReply(int);
    void setRendererViewport(int, int, int, int, float, float, float, float);
}

-keep class com.termux.x11.CmdEntryPoint {
    <init>(java.lang.String[]);
    public static void main(java.lang.String[]);
    native <methods>;
    void sendBroadcast();
    void sendBroadcastDelayed();
    void setDocumentTag(java.lang.String);
}

# CmdEntryPoint reaches these hidden framework classes via reflection; they
# exist at runtime but aren't in the public SDK stub jar R8 resolves against.
-dontwarn android.app.ActivityThread
-dontwarn android.app.ContextImpl
-dontwarn android.app.IActivityManager
-dontwarn android.content.IIntentReceiver
-dontwarn android.content.IIntentReceiver$Stub
-dontwarn android.content.IIntentSender
-dontwarn android.content.pm.IPackageManager

-keepclassmembers class com.termux.x11.** {
    void performReceive(android.content.Intent, int, java.lang.String, android.os.Bundle, boolean, boolean, int);
}

# Keep Preference subclasses' onSetInitialValue for reflective calls
-keepclassmembers class * extends android.preference.Preference {
    void onSetInitialValue(boolean, java.lang.Object);
}
