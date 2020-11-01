package com.everstake.staking.sdk.ui.stake

import androidx.lifecycle.viewModelScope
import com.everstake.staking.sdk.data.usecase.UpdateCoinDetailsUseCase
import com.everstake.staking.sdk.data.usecase.UpdateValidatorListUseCase
import com.everstake.staking.sdk.ui.base.BaseViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.launch

/**
 * created by Alex Ivanov on 01.11.2020.
 */
@FlowPreview
@ExperimentalCoroutinesApi
internal class StakeViewModel : BaseViewModel() {

    private val updateCoinUseCase: UpdateCoinDetailsUseCase by lazy { UpdateCoinDetailsUseCase() }
    private val updateValidatorUseCase: UpdateValidatorListUseCase by lazy { UpdateValidatorListUseCase() }

    private val coinIdChannel: BroadcastChannel<String> = ConflatedBroadcastChannel()
    private val validatorIdChannel: BroadcastChannel<String?> = ConflatedBroadcastChannel()
    private val amountChannel: BroadcastChannel<String> = ConflatedBroadcastChannel()

    init {
        amountChannel.offer("0")
    }

    fun updateCoinId(coinId: String) {
        coinIdChannel.offer(coinId)
        validatorIdChannel.offer(null)
        viewModelScope.launch {
            val updateCoin = async { updateCoinUseCase.updateData() }
            val updateValidator = async { updateValidatorUseCase.updateValidators(coinId) }
            updateCoin.await()
            updateValidator.await()
        }
    }

    fun getCoinId(): String = "0" // TODO add real data

    fun updateValidatorId(validatorId: String) =
        validatorIdChannel.offer(validatorId)

    fun getValidatorId(): String = "0" // TODO add real data

    fun updateAmount(amount: String) =
        amountChannel.offer(amount)
}