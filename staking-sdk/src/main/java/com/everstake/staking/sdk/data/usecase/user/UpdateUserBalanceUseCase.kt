package com.everstake.staking.sdk.data.usecase.user

import com.everstake.staking.sdk.EverstakeBalanceModel
import com.everstake.staking.sdk.data.repository.UserBalanceRepository

/**
 * created by Alex Ivanov on 04.11.2020.
 */
internal class UpdateUserBalanceUseCase(
    private val userRepository: UserBalanceRepository = UserBalanceRepository.instance
) {

    fun updateUserInfo(balanceList: List<EverstakeBalanceModel>) {
        userRepository.updateBalance(balanceList)
    }
}