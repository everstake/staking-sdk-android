package com.everstake.staking.sdk.ui.coin.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.everstake.staking.sdk.data.model.ui.CoinListModel
import com.everstake.staking.sdk.data.model.ui.SectionData
import com.everstake.staking.sdk.data.usecase.GetCoinListUseCase
import com.everstake.staking.sdk.data.usecase.UpdateCoinDetailsUseCase
import com.everstake.staking.sdk.ui.base.BaseViewModel
import kotlinx.coroutines.launch

/**
 * created by Alex Ivanov on 07.10.2020.
 */
internal class CoinListViewModel : BaseViewModel() {

    private val getCoinListUseCase: GetCoinListUseCase by lazy { GetCoinListUseCase() }
    private val updateCoinListUseCase: UpdateCoinDetailsUseCase by lazy { UpdateCoinDetailsUseCase() }

    val sectionData: LiveData<List<SectionData<CoinListModel>>> =
        getCoinListUseCase.getCoinListUIFlow().asLiveData()

    init {
        viewModelScope.launch {
            updateCoinListUseCase.updateData()
        }
    }
}