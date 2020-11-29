package com.everstake.staking.sdk.ui.coin.select

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.everstake.staking.sdk.R
import com.everstake.staking.sdk.data.model.ui.CoinSelectModel
import com.everstake.staking.sdk.ui.base.BaseActivity
import com.everstake.staking.sdk.ui.base.list.RecyclerClickListener
import com.everstake.staking.sdk.ui.base.list.decorator.DividerDecorator
import com.everstake.staking.sdk.util.dpToPx
import kotlinx.android.synthetic.main.activity_coin_select.*
import kotlinx.coroutines.launch

/**
 * created by Alex Ivanov on 23.10.2020.
 */
internal class CoinSelectActivity : BaseActivity<CoinSelectViewModel>() {

    companion object {
        private const val RESULT_KEY_COIN_ID = "Result.CoinId"
        fun getIntent(context: Context): Intent = Intent(context, CoinSelectActivity::class.java)

        private fun getResultIntent(coinId: String): Intent =
            Intent().putExtra(RESULT_KEY_COIN_ID, coinId)

        fun getCoinIdFromResult(result: Intent): String? =
            result.getStringExtra(RESULT_KEY_COIN_ID)
    }

    private val adapter: CoinSelectAdapter by lazy { CoinSelectAdapter() }
    private val dividerDecorator: DividerDecorator by lazy {
        DividerDecorator(marginLeft = dpToPx(72))
    }
    private val clickListener: RecyclerClickListener<CoinSelectModel> =
        object : RecyclerClickListener<CoinSelectModel>() {
            override fun onClick(pos: Int, model: CoinSelectModel?) {
                model ?: return
                setResult(Activity.RESULT_OK, getResultIntent(model.id))
                onBackPressed()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(selectCoinToolbar)
        selectCoinRecycler.layoutManager = LinearLayoutManager(this)
        selectCoinRecycler.adapter = adapter
        adapter.setClickListener(clickListener)
        selectCoinRecycler.addItemDecoration(dividerDecorator)

        viewModel.listData.observe(this) {
            updateList(it)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> onBackPressed().let { true }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        selectCoinRecycler.removeItemDecoration(dividerDecorator)
    }

    override fun provideLayoutRes(): Int = R.layout.activity_coin_select

    override fun provideViewModel(): CoinSelectViewModel =
        ViewModelProvider(this).get(CoinSelectViewModel::class.java)

    private fun updateList(list: List<CoinSelectModel>) {
        lifecycleScope.launch {
            adapter.applyChanges(list)
        }
    }
}