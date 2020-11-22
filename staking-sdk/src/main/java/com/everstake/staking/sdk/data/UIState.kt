package com.everstake.staking.sdk.data

/**
 * created by Alex Ivanov on 11/15/20.
 */
internal sealed class State<out T> {
    object Progress : State<Nothing>()
    data class Result<out T>(val result: T) : State<T>()
    data class Error(val errorMessage: String? = null) : State<Nothing>()
}