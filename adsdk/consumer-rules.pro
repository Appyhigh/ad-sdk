-keep class io.jsonwebtoken.** { *; }
-keep class io.jsonwebtoken.impl.** { *; }
-keep class io.jsonwebtoken.*.* { *; }
-keepnames class io.jsonwebtoken.* { *; }
-keep interface io.jsonwebtoken.** { *; }
-keep interface io.jsonwebtoken.impl.** { *; }
-keep interface io.jsonwebtoken.*.* { *; }
-keepnames interface io.jsonwebtoken.* { *; }
-keep class com.appyhigh.adsdk.data.model.adresponse.** { *; }

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken