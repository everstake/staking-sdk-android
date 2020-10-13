package com.everstake.staking.sdk.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

/**
 * created by Alex Ivanov on 10.10.2020.
 */
internal inline fun <reified T> Gson.parseWithType(json: String): T {
    val listType: Type = object : TypeToken<T>() {}.type
    return fromJson(json, listType)
}