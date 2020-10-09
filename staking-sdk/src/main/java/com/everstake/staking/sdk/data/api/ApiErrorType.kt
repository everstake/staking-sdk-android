package com.everstake.staking.sdk.data.api

/**
 * created by Alex Ivanov on 09.10.2020.
 */
enum class ApiErrorType(private val httpCode: Int) {
    RequestTimeout(-2),
    NoInternet(-1),
    Unknown(0),
    BadRequest(400),
    Unauthorized(401),
    Forbidden(403),
    NotFound(404),
    InternalServerError(500),
    ServiceUnavailable(503);

    companion object {
        fun valueOf(errorCode: Int): ApiErrorType =
            values().find { it.httpCode == errorCode } ?: Unknown
    }
}