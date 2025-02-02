# Preserve the line number information for debugging stack traces.
-keepattributes SourceFile,LineNumberTable,InnerClasses

#Keep models
-keep class com.everstake.staking.sdk.data.model.api.** { *; }

-keep class com.everstake.staking.sdk.EverstakeAction { *; }
-keep class com.everstake.staking.sdk.EverstakeBalanceModel { *; }
-keep class com.everstake.staking.sdk.EverstakeListener { *; }
-keep class com.everstake.staking.sdk.EverstakeStaking { *; }
-keep class com.everstake.staking.sdk.ValidatorInfo { *; }