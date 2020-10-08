package com.everstake.staking.sdk.util

import android.content.Context
import android.text.format.DateUtils
import com.google.gson.Gson
import java.io.*
import java.util.*
import kotlin.coroutines.suspendCoroutine

/**
 * created by Alex Ivanov on 07.10.2020.
 */

internal enum class CacheType {
    COIN,
    STAKE;

    fun cacheFileName(): String {
        return "${this.name.toLowerCase(Locale.ENGLISH)}_cache.json"
    }
}

internal data class CacheData(
    val dataJson: String,
    val serializationTimestamp: Long = System.currentTimeMillis()
)

private val gson: Gson = Gson()

/**
 * Store data into special cache file with date of storage
 * @param context required for File IO
 * @param cacheType type of cache to use
 * @param data data to serialize into file
 */
internal inline fun <reified T> storeCache(
    context: Context,
    cacheType: CacheType,
    data: T
) {
    val dataStr: String = gson.toJson(data)
    val cacheData: String = gson.toJson(CacheData(dataStr))
    val fileWriter = FileWriter(File(context.filesDir, cacheType.cacheFileName()))
    BufferedWriter(fileWriter).use { writer: BufferedWriter ->
        writer.write(cacheData)
    }
}

/**
 * Retrieve data from previously stored file
 * @param context required for file IO
 * @param cacheType type of cache file to use
 * @param cacheInvalidateTimeout time after which cache data will be considered as expired
 * @return cached data if there is any or if it's not expired
 */
internal inline fun <reified T> readCache(
    context: Context,
    cacheType: CacheType,
    cacheInvalidateTimeout: Long = DateUtils.DAY_IN_MILLIS
): T? {
    val cacheFile = File(context.filesDir, cacheType.cacheFileName())
    if (!cacheFile.exists()) return null
    val fileReader = FileReader(cacheFile)
    val text: String = BufferedReader(fileReader).use { reader: BufferedReader ->
        reader.readText()
    }
    if (text.isBlank()) return null
    val cacheData: CacheData = gson.fromJson(text, CacheData::class.java)
    return if (System.currentTimeMillis() > cacheData.serializationTimestamp + cacheInvalidateTimeout) null
    else gson.fromJson(cacheData.dataJson, T::class.java)
}