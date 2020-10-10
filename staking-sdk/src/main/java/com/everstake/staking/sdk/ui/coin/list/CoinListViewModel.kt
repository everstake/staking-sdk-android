package com.everstake.staking.sdk.ui.coin.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.everstake.staking.sdk.data.model.ui.CoinListModel
import com.everstake.staking.sdk.data.model.ui.SectionData
import com.everstake.staking.sdk.data.usecase.GetCoinListUseCase
import com.everstake.staking.sdk.ui.base.BaseViewModel
import kotlinx.coroutines.launch

/**
 * created by Alex Ivanov on 07.10.2020.
 */
internal class CoinListViewModel : BaseViewModel<CoinListNavigator>() {

    private val coinListUseCase: GetCoinListUseCase by lazy { GetCoinListUseCase() }

    val sectionData: LiveData<List<SectionData<CoinListModel>>> =
        coinListUseCase.getCoinListUIFlow().asLiveData()

    init {
        viewModelScope.launch {
            coinListUseCase.updateData()
        }
    }
}