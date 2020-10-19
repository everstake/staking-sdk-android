package com.everstake.staking.sdk.ui.coin.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.everstake.staking.sdk.data.model.ui.CoinDetailsModel
import com.everstake.staking.sdk.data.usecase.GetCoinDetailsUseCase
import com.everstake.staking.sdk.data.usecase.UpdateCoinDetailsUseCase
import com.everstake.staking.sdk.ui.base.BaseViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * created by Alex Ivanov on 16.10.2020.
 */
internal class CoinDetailsViewModel : BaseViewModel() {

    private val coinIdChannel: Channel<String> = Channel(CONFLATED)
    private val updateCoinsUseCase: UpdateCoinDetailsUseCase by lazy { UpdateCoinDetailsUseCase() }
    private val coinDetailsUseCase: GetCoinDetailsUseCase by lazy { GetCoinDetailsUseCase() }

    val coinDetails: LiveData<CoinDetailsModel> = coinDetailsUseCase
        .getCoinDetailsFlow(coinIdChannel.receiveAsFlow().distinctUntilChanged())
        .asLiveData(viewModelScope.coroutineContext)

    init {
        viewModelScope.launch {
            updateCoinsUseCase.updateData()
        }
    }

    fun setCoinId(coinId: String) {
        viewModelScope.launch {
            coinIdChannel.send(coinId)
        }
    }

}