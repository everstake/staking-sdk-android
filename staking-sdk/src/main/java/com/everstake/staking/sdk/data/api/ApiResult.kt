package com.everstake.staking.sdk.data.api

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * created by Alex Ivanov on 09.10.2020.
 */
internal sealed class ApiResult<out T> {

    class Success<out T>(val result: T) : ApiResult<T>()

    class Error(val errorType: ApiErrorType, val exception: Throwable? = null) : ApiResult<Nothing>() {
        constructor(httpException: HttpException) : this(
            ApiErrorType.valueOf(httpException.code()),
            httpException
        )

        companion object {
            fun handleException(e: Throwable): Error = when (e) {
                is HttpException -> Error(e)
                is UnknownHostException, is IOException -> Error(ApiErrorType.NoInternet, e)
                is SocketTimeoutException -> Error(ApiErrorType.RequestTimeout, e)
                else -> Error(ApiErrorType.Unknown, e)
            }
        }

    }
}