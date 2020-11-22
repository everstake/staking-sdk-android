package com.everstake.staking.sdk.data.model.api

import java.math.BigDecimal

/**
 * created by Alex Ivanov on 09.10.2020.
 */
internal data class PutStakeBodyModel(val id: String, val address: String)

internal data class PutStakeResponseModel(
    val coinId: String,
    val amount: BigDecimal,
    val amountToClaim: BigDecimal?,
    val validator: Validator?
)