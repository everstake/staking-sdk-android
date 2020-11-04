package com.everstake.staking.sdk.ui.coin.select

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.everstake.staking.sdk.data.model.ui.CoinSelectModel
import com.everstake.staking.sdk.data.usecase.coins.GetCoinListForSelectUseCase
import com.everstake.staking.sdk.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers

/**
 * created by Alex Ivanov on 23.10.2020.
 */
internal class CoinSelectViewModel : BaseViewModel() {

    private val getCoinsUseCase: GetCoinListForSelectUseCase by lazy { GetCoinListForSelectUseCase() }

    val listData: LiveData<List<CoinSelectModel>> = getCoinsUseCase
        .getCoinListUIFlow()
        .asLiveData(Dispatchers.IO)

}