package com.everstake.staking.sdk.ui.calculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.everstake.staking.sdk.data.model.ui.CalculatorModel
import com.everstake.staking.sdk.data.usecase.CalculatorUseCase
import com.everstake.staking.sdk.data.usecase.stake.UpdateCoinDetailsUseCase
import com.everstake.staking.sdk.data.usecase.validator.UpdateValidatorListUseCase
import com.everstake.staking.sdk.ui.base.BaseViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * created by Alex Ivanov on 19.10.2020.
 */
@ExperimentalCoroutinesApi
@FlowPreview
internal class CalculatorViewModel : BaseViewModel() {

    private val updateCoinUseCase: UpdateCoinDetailsUseCase by lazy { UpdateCoinDetailsUseCase() }
    private val updateValidatorUseCase: UpdateValidatorListUseCase by lazy { UpdateValidatorListUseCase() }
    private val calculatorUseCase: CalculatorUseCase by lazy { CalculatorUseCase() }

    private val coinIdChannel: BroadcastChannel<String> = ConflatedBroadcastChannel()
    private val selectedValidatorChannel: BroadcastChannel<List<String>> =
        ConflatedBroadcastChannel()
    private val amountChannel: BroadcastChannel<String> = ConflatedBroadcastChannel()
    private val includeFee: BroadcastChannel<Boolean> = ConflatedBroadcastChannel()
    private val includeReinvest: BroadcastChannel<Boolean> = ConflatedBroadcastChannel()

    val calculatorData: LiveData<CalculatorModel> = calculatorUseCase.getCalculatorDataFlow(
        coinIdChannel.asFlow().distinctUntilChanged(),
        selectedValidatorChannel.asFlow().distinctUntilChanged(),
        amountChannel.asFlow().distinctUntilChanged(),
        includeFee.asFlow().distinctUntilChanged(),
        includeReinvest.asFlow().distinctUntilChanged()
    ).asLiveData(Dispatchers.IO)

    init {
        amountChannel.offer("0")
        selectedValidatorChannel.offer(emptyList())
        includeFee.offer(false)
        includeReinvest.offer(false)
    }

    fun updateCoinId(coinId: String) {
        if (calculatorData.value?.coinId == coinId) return
        coinIdChannel.offer(coinId)
        selectedValidatorChannel.offer(emptyList())
        viewModelScope.launch {
            val updateCoin = async { updateCoinUseCase.updateData() }
            val updateValidator = async { updateValidatorUseCase.updateValidators() }
            updateCoin.await()
            updateValidator.await()
        }
    }

    fun getCoinId(): String = calculatorData.value?.coinId ?: ""

    fun updateSelectedValidators(selectedValidators: List<String>) =
        selectedValidatorChannel.offer(selectedValidators)

    fun getSelectedValidators(): List<String> =
        calculatorData.value?.validators?.map { it.validatorId } ?: emptyList()

    fun updateAmount(amount: String) =
        amountChannel.offer(amount)

    fun updateIncludeFee(includeFee: Boolean) =
        this.includeFee.offer(includeFee)

    fun updateIncludeReinvest(includeReinvest: Boolean) =
        this.includeReinvest.offer(includeReinvest)

    fun allowMultiValidator(): Boolean = calculatorData.value?.allowMultipleValidator ?: false
}