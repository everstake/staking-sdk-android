package com.everstake.staking.sdk.ui.unstake

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.everstake.staking.sdk.data.model.ui.UnstakeModel
import com.everstake.staking.sdk.data.usecase.GetUnstakeInfoUseCase
import com.everstake.staking.sdk.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.math.BigDecimal

/**
 * created by Alex Ivanov on 30.10.2020.
 */
@FlowPreview
@ExperimentalCoroutinesApi
internal class UnstakeViewModel : BaseViewModel() {

    private val unstakeUseCase: GetUnstakeInfoUseCase = GetUnstakeInfoUseCase()

    private val coinIdChannel: BroadcastChannel<String> = ConflatedBroadcastChannel()
    private val selectedValidatorsChannel: BroadcastChannel<List<String>> =
        ConflatedBroadcastChannel()
    private val amountChannel: BroadcastChannel<String> = ConflatedBroadcastChannel()
    private val progressChannel: BroadcastChannel<BigDecimal> = ConflatedBroadcastChannel()

    val unstakeModel: LiveData<UnstakeModel> = unstakeUseCase.getUnstakeFlow(
        coinIdFlow = coinIdChannel.asFlow().distinctUntilChanged(),
        selectedValidatorsFlow = selectedValidatorsChannel.asFlow().distinctUntilChanged(),
        amountFlow = amountChannel.asFlow().distinctUntilChanged(),
        progressFlow = progressChannel.asFlow().distinctUntilChanged()
    ).asLiveData(Dispatchers.IO)

    init {
        selectedValidatorsChannel.offer(emptyList())
        amountChannel.offer("")
        progressChannel.offer(BigDecimal.ZERO)
    }

    fun initViewModel(coinId: String, validators: List<String>) {
        coinIdChannel.offer(coinId)
        selectedValidatorsChannel.offer(validators)
    }

    fun updateAmount(amount: String) {
        if (unstakeModel.value?.amount == amount) return
        amountChannel.offer(amount)
    }

    fun updateProgress(progress: BigDecimal) {
        if (unstakeModel.value?.progress == progress) return
        progressChannel.offer(progress)
    }
}