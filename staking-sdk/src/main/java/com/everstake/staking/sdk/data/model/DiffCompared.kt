package com.everstake.staking.sdk.data.model

/**
 * created by Alex Ivanov on 08.10.2020.
 */
internal interface DiffCompared {
    fun idEquals(obj: Any): Boolean
    fun uiEquals(obj: Any): Boolean
}