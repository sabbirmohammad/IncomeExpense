# ==============================================================================
# ExpenseTracker - ProGuard & R8 Optimization Rules
# ==============================================================================

# ------------------------------------------------------------------------------
# 1. General Optimization & Attributes
# ------------------------------------------------------------------------------
-keepattributes *Annotation*, EnclosingMethod, InnerClasses, Signature, SourceFile, LineNumberTable

# ------------------------------------------------------------------------------
# 2. Kotlinx Serialization
# ------------------------------------------------------------------------------
-keepattributes *Annotation*
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}
-keepclassmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}
-keepclassmembers class com.example.expensetracker.data.model.** {
    <fields>;
    <init>(...);
    public static ** Companion;
}
-keep class com.example.expensetracker.data.model.** { *; }
-dontwarn kotlinx.serialization.**

# ------------------------------------------------------------------------------
# 3. Google Drive API Client & OAuth
# ------------------------------------------------------------------------------
-keep class com.google.api.client.** { *; }
-keep interface com.google.api.client.** { *; }
-keep class com.google.api.services.drive.** { *; }
-keep class com.google.android.gms.auth.api.signin.** { *; }
-keep class com.google.android.gms.common.** { *; }
-dontwarn com.google.api.client.**
-dontwarn com.google.api.services.drive.**
-dontwarn org.apache.http.**
-dontwarn com.google.common.**
-dontwarn com.google.errorprone.annotations.**
-dontwarn javax.annotation.**

# ------------------------------------------------------------------------------
# 4. AndroidX WorkManager, Room & App Startup
# ------------------------------------------------------------------------------
-keep class * extends androidx.work.Worker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}
-keep class * extends androidx.work.CoroutineWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}
-keep class com.example.expensetracker.data.worker.DailySyncWorker { *; }
-keep class androidx.work.impl.** { *; }
-keep class androidx.work.** { *; }
-dontwarn androidx.work.impl.**

# Room Database reflection used by WorkManager
-keep class * extends androidx.room.RoomDatabase {
    public <init>();
    <init>();
}
-keep class androidx.work.impl.WorkDatabase_Impl {
    public <init>();
    <init>();
}

# AndroidX Startup
-keep class * extends androidx.startup.Initializer {
    public <init>();
}

# ------------------------------------------------------------------------------
# 5. Database & Local Storage
# ------------------------------------------------------------------------------
-keep class com.example.expensetracker.data.local.** { *; }

# ------------------------------------------------------------------------------
# 6. Jetpack Compose & Material 3
# ------------------------------------------------------------------------------
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}
-keep class androidx.compose.material.icons.** { *; }

# ------------------------------------------------------------------------------
# 7. Android Architecture Components & ViewModels
# ------------------------------------------------------------------------------
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    public <init>(...);
}
-keep class * extends androidx.lifecycle.ViewModel
-keep class * extends androidx.lifecycle.AndroidViewModel {
    public <init>(android.app.Application);
}
-keep class com.example.expensetracker.ui.**.*ViewModel* { *; }

# ------------------------------------------------------------------------------
# 8. App Core, Repository, Services & UI
# ------------------------------------------------------------------------------
-keep class com.example.expensetracker.data.repository.** { *; }
-keep class com.example.expensetracker.data.drive.** { *; }
-keep class com.example.expensetracker.ui.** { *; }
-keep class com.example.expensetracker.theme.** { *; }
-keep class com.example.expensetracker.common.** { *; }
