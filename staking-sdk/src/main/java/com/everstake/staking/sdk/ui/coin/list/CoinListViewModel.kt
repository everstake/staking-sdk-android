package com.everstake.staking.sdk.ui.coin.list

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.everstake.staking.sdk.data.repository.CoinListRepository
import com.everstake.staking.sdk.ui.base.BaseViewModel
import com.google.gson.Gson
import kotlinx.coroutines.launch

/**
 * created by Alex Ivanov on 07.10.2020.
 */
internal class CoinListViewModel : BaseViewModel<CoinListNavigator>() {

    init {
        viewModelScope.launch {
            val result = CoinListRepository.instance.loadCoinList()
            Log.d("<<SS", Gson().toJson(result))
        }
    }
}