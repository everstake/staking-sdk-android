package com.everstake.staking.sdk.ui.stake

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.everstake.staking.sdk.data.model.ui.StakeModel
import com.everstake.staking.sdk.data.usecase.GetStakeInfoUseCase
import com.everstake.staking.sdk.data.usecase.stake.UpdateCoinDetailsUseCase
import com.everstake.staking.sdk.data.usecase.validator.UpdateValidatorListUseCase
import com.everstake.staking.sdk.ui.base.BaseViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.math.BigDecimal

/**
 * created by Alex Ivanov on 01.11.2020.
 */
@FlowPreview
@ExperimentalCoroutinesApi
internal class StakeViewModel : BaseViewModel() {

    private val updateCoinUseCase: UpdateCoinDetailsUseCase by lazy { UpdateCoinDetailsUseCase() }
    private val updateValidatorUseCase: UpdateValidatorListUseCase by lazy { UpdateValidatorListUseCase() }
    private val stakeInfoUseCase: GetStakeInfoUseCase by lazy { GetStakeInfoUseCase() }

    private val coinIdChannel: BroadcastChannel<String> = ConflatedBroadcastChannel()
    private val validatorIdChannel: BroadcastChannel<String?> = ConflatedBroadcastChannel()
    private val amountChannel: BroadcastChannel<String> = ConflatedBroadcastChannel()
    private val progressChannel: BroadcastChannel<BigDecimal> = ConflatedBroadcastChannel()

    val stakeInfo: LiveData<StakeModel> = stakeInfoUseCase.getStakeFlow(
        coinIdChannel.asFlow().distinctUntilChanged(),
        validatorIdChannel.asFlow().distinctUntilChanged(),
        amountChannel.asFlow().distinctUntilChanged(),
        progressChannel.asFlow().distinctUntilChanged()
    ).asLiveData(Dispatchers.IO)

    init {
        amountChannel.offer("")
        progressChannel.offer(BigDecimal.ZERO)
    }

    fun updateCoinId(coinId: String) {
        coinIdChannel.offer(coinId)
        validatorIdChannel.offer(null)
        viewModelScope.launch {
            val updateCoin = async { updateCoinUseCase.updateData() }
            val updateValidator = async { updateValidatorUseCase.updateValidators() }
            updateCoin.await()
            updateValidator.await()
        }
    }

    fun getCoinId(): String = stakeInfo.value?.coinId ?: ""

    fun updateValidatorId(validatorId: String) =
        validatorIdChannel.offer(validatorId)

    fun getValidatorId(): String = stakeInfo.value?.validatorId ?: ""

    fun updateAmount(amount: String) {
        if (stakeInfo.value?.amount == amount) return
        amountChannel.offer(amount)
    }

    fun updateProgress(progress: BigDecimal) {
        if (stakeInfo.value?.progress == progress) return
        progressChannel.offer(progress)
    }
}