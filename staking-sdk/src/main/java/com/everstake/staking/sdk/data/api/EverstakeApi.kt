package com.everstake.staking.sdk.data.api

import com.everstake.staking.sdk.data.model.api.GetCoinsResponseModel
import com.everstake.staking.sdk.data.model.api.PutStakeBodyModel
import com.everstake.staking.sdk.data.model.api.PutStakeResponseModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

/**
 * created by Alex Ivanov on 09.10.2020.
 */
internal interface EverstakeApi {

    @GET("/coin")
    suspend fun getSupportedCoins(): List<GetCoinsResponseModel>

    @PUT("/stake")
    suspend fun getStakedInfo(@Body body: PutStakeBodyModel): List<PutStakeResponseModel>
}