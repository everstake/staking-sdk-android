package com.everstake.staking.sdk.ui.coin.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.everstake.staking.sdk.data.State
import com.everstake.staking.sdk.data.model.ui.CoinListModel
import com.everstake.staking.sdk.data.model.ui.SectionData
import com.everstake.staking.sdk.data.usecase.GetCoinListUseCase
import com.everstake.staking.sdk.data.usecase.stake.UpdateCoinDetailsUseCase
import com.everstake.staking.sdk.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * created by Alex Ivanov on 07.10.2020.
 */
internal class CoinListViewModel : BaseViewModel() {

    private val getCoinListUseCase: GetCoinListUseCase by lazy { GetCoinListUseCase() }
    private val updateCoinListUseCase: UpdateCoinDetailsUseCase by lazy { UpdateCoinDetailsUseCase() }

    val state: MutableLiveData<State<List<SectionData<CoinListModel>>>> = MutableLiveData()

    init {
        refreshData(false)
        viewModelScope.launch {
            getCoinListUseCase
                .getCoinListUIFlow()
                .flowOn(Dispatchers.IO)
                .collect { list: List<SectionData<CoinListModel>> ->
                    state.postValue(State.Result(list))
                }
        }
    }

    fun refreshData(forceUpdate: Boolean = true) {
        state.value = State.Progress
        viewModelScope.launch {
            val updateTimeout: Long = if (forceUpdate) 0L
            else TimeUnit.MINUTES.toMillis(10)
            val success: Boolean = updateCoinListUseCase.updateData(updateTimeout)
            if (!success && state.value !is State.Result) {
                state.postValue(State.Error())
            }
        }
    }
}