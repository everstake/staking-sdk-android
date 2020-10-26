package com.everstake.staking.sdk.ui.validator.select

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.everstake.staking.sdk.data.model.ui.ValidatorListModel
import com.everstake.staking.sdk.data.usecase.GetValidatorsListUseCase
import com.everstake.staking.sdk.data.usecase.UpdateValidatorListUseCase
import com.everstake.staking.sdk.ui.base.BaseViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * created by Alex Ivanov on 23.10.2020.
 */
@FlowPreview
@ExperimentalCoroutinesApi
internal class ValidatorSelectViewModel : BaseViewModel() {

    private val getValidatorUseCase: GetValidatorsListUseCase by lazy { GetValidatorsListUseCase() }
    private val updateValidatorsUseCase: UpdateValidatorListUseCase by lazy { UpdateValidatorListUseCase() }

    private val coinIdChannel: BroadcastChannel<String> = BroadcastChannel(Channel.CONFLATED)
    private val selectedValidatorChannel: BroadcastChannel<String> =
        BroadcastChannel(Channel.CONFLATED)

    val listData: LiveData<List<ValidatorListModel>> = getValidatorUseCase.getValidatorListFlow(
        coinIdChannel.asFlow().distinctUntilChanged(),
        selectedValidatorChannel.asFlow().distinctUntilChanged()
    ).asLiveData(viewModelScope.coroutineContext)

    fun initViewModel(coinId: String, validatorId: String?) {
        viewModelScope.launch {
            coinIdChannel.send(coinId)
            selectedValidatorChannel.send(validatorId ?: "")
            updateValidatorsUseCase.updateValidators(coinId)
        }
    }
}