package com.everstake.staking.sdk.data.api

import com.everstake.staking.sdk.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

/**
 * created by Alex Ivanov on 09.10.2020.
 */
private object ApiProvider {

    val everstakeApi: EverstakeApi by lazy {
        provideAPIService(EverstakeApi::class, "https://wallet-sdk-static.herokuapp.com/")
    }

    private fun <T : Any> provideAPIService(serviceClass: KClass<T>, url: String): T =
        provideAPIService(serviceClass.java, url)

    private fun <T : Any> provideAPIService(serviceClass: Class<T>, url: String): T {
        val clientBuilder = OkHttpClient.Builder()

        //ADDING HEADERS
        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            clientBuilder.addInterceptor(interceptor)
        }

        val client = clientBuilder.connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .build()

        val gson: Gson = GsonBuilder().setLenient().create()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(url)
            .client(client)
            .build()

        return retrofit.create(serviceClass)
    }
}

internal suspend fun <R> callEverstakeApi(block: suspend (api: EverstakeApi) -> R): ApiResult<R> =
    try {
        ApiResult.Success(block(ApiProvider.everstakeApi))
    } catch (throwable: Throwable) {
        ApiResult.Error.handleException(throwable)
    }