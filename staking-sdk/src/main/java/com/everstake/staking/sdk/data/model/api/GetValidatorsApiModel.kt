package com.everstake.staking.sdk.data.model.api

import java.math.BigDecimal

/**
 * created by Alex Ivanov on 15.10.2020.
 */
internal data class GetValidatorsApiResponse(
    val id: String,
    val validatorName: String,
    val fee: BigDecimal,
    val isReliable: Boolean
)