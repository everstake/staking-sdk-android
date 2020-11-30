# Preserve the line number information for debugging stack traces.
-keepattributes SourceFile,LineNumberTable,InnerClasses

#Keep models
-keep class com.everstake.staking.sdk.data.model.api.** { *; }