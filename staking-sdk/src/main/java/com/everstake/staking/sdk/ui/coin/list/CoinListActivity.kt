package com.everstake.staking.sdk.ui.coin.list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.everstake.staking.sdk.R
import com.everstake.staking.sdk.data.model.ui.CoinListModel
import com.everstake.staking.sdk.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_coin_list.*

/**
 * created by Alex Ivanov on 07.10.2020.
 */
internal class CoinListActivity : BaseActivity<CoinListViewModel, CoinListNavigator>() {

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, CoinListActivity::class.java)
    }

    private val adapter: CoinListAdapter by lazy { CoinListAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        coinListRecycler.layoutManager = LinearLayoutManager(this)
        coinListRecycler.adapter = adapter
        adapter.setNewData(
            listOf(
                CoinListModel(
                    "0",
                    "Tezos",
                    "https://s2.coinmarketcap.com/static/img/coins/64x64/2011.png",
                    "10%",
                    true,
                    "1000 XTZ"
                ),
                CoinListModel(
                    "1",
                    "Cosmos",
                    "https://s2.coinmarketcap.com/static/img/coins/64x64/3794.png",
                    "20%",
                    true,
                    null
                ),
                CoinListModel(
                    "2",
                    "BAND",
                    "https://s2.coinmarketcap.com/static/img/coins/64x64/4679.png",
                    "0%",
                    false,
                    null
                )
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.coin_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> onBackPressed().let { true }
        R.id.menu_info -> {
            Toast.makeText(this, "Not implemented", Toast.LENGTH_SHORT).show()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun provideLayoutRes(): Int = R.layout.activity_coin_list

    override fun provideViewModel(): CoinListViewModel =
        ViewModelProvider(this).get(CoinListViewModel::class.java)

    override fun provideNavigator(): CoinListNavigator = object : CoinListNavigator {}
}