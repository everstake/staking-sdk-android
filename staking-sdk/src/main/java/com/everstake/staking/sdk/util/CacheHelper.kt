package com.everstake.staking.sdk.util

import android.content.Context
import android.text.format.DateUtils
import com.google.gson.Gson
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import java.io.*
import java.util.*

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
) {
    companion object {
        val empty: CacheData = CacheData("", 0)
    }
}

private val gson: Gson = Gson()

private val updateChannels: Map<CacheType, Channel<Unit>> = CacheType.values().map {
    it to Channel<Unit>(CONFLATED)
}.toMap()

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
    updateChannels[cacheType]?.offer(Unit)
}

/**
 * Read data from file
 * @param context required for file IO
 * @param cacheType type of cache file to use
 * @return cache data or null if file doesn't exists or file is empty
 */
internal fun readCacheFile(
    context: Context,
    cacheType: CacheType
): CacheData? {
    val cacheFile = File(context.filesDir, cacheType.cacheFileName())
    if (!cacheFile.exists()) return null
    val fileReader = FileReader(cacheFile)
    val text: String = BufferedReader(fileReader).use { reader: BufferedReader ->
        reader.readText()
    }
    if (text.isBlank()) return null
    return gson.fromJson(text, CacheData::class.java)
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
    val cacheData: CacheData = readCacheFile(context, cacheType) ?: return null
    return if (System.currentTimeMillis() > cacheData.serializationTimestamp + cacheInvalidateTimeout) null
    else gson.fromJson(cacheData.dataJson, T::class.java)
}

internal fun readCacheAsFlow(
    context: Context,
    cacheType: CacheType
): Flow<CacheData> = flow {
    val channel: Channel<Unit> = updateChannels[cacheType]
        ?: throw IllegalStateException("Missing Channel for type: $cacheType")

    emit(readCacheFile(context, cacheType) ?: CacheData.empty)
    for (signal in channel) {
        val result: CacheData = readCacheFile(context, cacheType) ?: CacheData.empty
        emit(result)
    }
}.onStart {
    val result: CacheData = readCacheFile(context, cacheType) ?: CacheData.empty
    emit(result)
}