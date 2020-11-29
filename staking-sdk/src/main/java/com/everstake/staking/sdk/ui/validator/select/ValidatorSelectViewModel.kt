package com.everstake.staking.sdk.ui.validator.select

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.everstake.staking.sdk.data.model.ui.ValidatorListModel
import com.everstake.staking.sdk.data.usecase.validator.GetValidatorsListUseCase
import com.everstake.staking.sdk.data.usecase.validator.UpdateValidatorListUseCase
import com.everstake.staking.sdk.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
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
    private val selectedValidatorChannel: BroadcastChannel<List<String>> =
        BroadcastChannel(Channel.CONFLATED)

    private var lastSelectedValidatorList: List<String> = emptyList()
    val listData: LiveData<List<ValidatorListModel>> = getValidatorUseCase.getValidatorListFlow(
        coinIdChannel.asFlow().distinctUntilChanged(),
        selectedValidatorChannel.asFlow().distinctUntilChanged()
    ).asLiveData(Dispatchers.IO)

    fun initViewModel(coinId: String, validatorIdArray: List<String>) {
        viewModelScope.launch {
            coinIdChannel.send(coinId)
            updateSelectedValidators(validatorIdArray)
            updateValidatorsUseCase.updateValidators()
        }
    }

    fun updateSelected(newValidatorModel: ValidatorListModel, allowMultiple: Boolean) {
        if (allowMultiple) {
            val currentSelection: List<String> = lastSelectedValidatorList
            val newSelection: List<String> = if (newValidatorModel.isSelected) {
                (currentSelection + arrayOf(newValidatorModel.id)).distinct()
            } else {
                currentSelection.filter { it != newValidatorModel.id }
            }
            updateSelectedValidators(newSelection)
        } else {
            updateSelectedValidators(listOf(newValidatorModel.id))
        }
    }

    fun getSelectedValidators(): List<String> {
        return lastSelectedValidatorList
    }

    private fun updateSelectedValidators(validators: List<String>) {
        lastSelectedValidatorList = validators
        selectedValidatorChannel.offer(validators)
    }
}