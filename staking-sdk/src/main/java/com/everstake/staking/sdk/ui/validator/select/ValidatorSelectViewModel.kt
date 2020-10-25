package com.everstake.staking.sdk.ui.validator.select

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.everstake.staking.sdk.data.model.ui.ValidatorListModel
import com.everstake.staking.sdk.data.usecase.GetValidatorsListUseCase
import com.everstake.staking.sdk.data.usecase.UpdateValidatorListUseCase
import com.everstake.staking.sdk.ui.base.BaseViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * created by Alex Ivanov on 23.10.2020.
 */
internal class ValidatorSelectViewModel : BaseViewModel() {

    private val getValidatorUseCase: GetValidatorsListUseCase by lazy { GetValidatorsListUseCase() }
    private val updateValidatorsUseCase: UpdateValidatorListUseCase by lazy { UpdateValidatorListUseCase() }

    private val coinIdChannel: Channel<String> = Channel(Channel.CONFLATED)
    private val selectedValidatorChannel: Channel<String> = Channel(Channel.CONFLATED)

    val listData: LiveData<List<ValidatorListModel>> = getValidatorUseCase.getValidatorListFlow(
        coinIdChannel.receiveAsFlow().distinctUntilChanged(),
        selectedValidatorChannel.receiveAsFlow().distinctUntilChanged()
    ).asLiveData(viewModelScope.coroutineContext)

    fun initViewModel(coinId: String, validatorId: String?) {
        viewModelScope.launch {
            coinIdChannel.send(coinId)
            selectedValidatorChannel.send(validatorId ?: "")
            updateValidatorsUseCase.updateValidators(coinId)
        }
    }
}