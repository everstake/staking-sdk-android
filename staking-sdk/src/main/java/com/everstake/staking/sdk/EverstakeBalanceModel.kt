package com.everstake.staking.sdk

/**
 * created by Alex Ivanov on 03.11.2020.
 */
data class EverstakeBalanceModel(
    val coinSymbol: String,
    val address: String,
    val balance: String // Balance in display format without symbol i.e 1.631
)