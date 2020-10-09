package com.everstake.staking.sdk.data.model.api

import java.math.BigDecimal

/**
 * created by Alex Ivanov on 09.10.2020.
 */
class PutStakeBodyModel(val id: String, val address: String)

class PutStakeResponseModel(
    val coinId: String,
    val amount: BigDecimal,
    val amountToClaim: BigDecimal?
)