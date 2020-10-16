package com.everstake.staking.sdk.ui.coin.list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.everstake.staking.sdk.R
import com.everstake.staking.sdk.data.model.ui.CoinListModel
import com.everstake.staking.sdk.data.model.ui.SectionData
import com.everstake.staking.sdk.ui.base.BaseActivity
import com.everstake.staking.sdk.ui.base.list.RecyclerClickListener
import com.everstake.staking.sdk.ui.base.list.decorator.DecoratorData
import com.everstake.staking.sdk.ui.base.list.decorator.DividerDecorator
import com.everstake.staking.sdk.ui.base.list.decorator.TextDividerDecorator
import com.everstake.staking.sdk.util.bindString
import com.everstake.staking.sdk.util.dpToPx
import kotlinx.android.synthetic.main.activity_coin_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * created by Alex Ivanov on 07.10.2020.
 */
internal class CoinListActivity : BaseActivity<CoinListViewModel, CoinListNavigator>() {

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, CoinListActivity::class.java)
    }

    private val adapter: CoinListAdapter by lazy { CoinListAdapter() }
    private val dividerDecorator: DividerDecorator by lazy {
        DividerDecorator(marginLeft = dpToPx(72))
    }
    private val textDecorator: TextDividerDecorator by lazy { TextDividerDecorator(this) }
    private val clickListener: RecyclerClickListener<CoinListModel> =
        object : RecyclerClickListener<CoinListModel>() {
            override fun onClick(pos: Int, model: CoinListModel?) {
                Toast.makeText(this@CoinListActivity, "Not Implemented", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        coinListRecycler.layoutManager = LinearLayoutManager(this)
        coinListRecycler.adapter = adapter
        adapter.setClickListener(clickListener)
        coinListRecycler.addItemDecoration(dividerDecorator)
        coinListRecycler.addItemDecoration(textDecorator)

        viewModel.sectionData.observe(this) {
            updateList(it)
        }
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

    override fun onDestroy() {
        super.onDestroy()
        coinListRecycler.removeItemDecoration(dividerDecorator)
        coinListRecycler.removeItemDecoration(textDecorator)
    }

    override fun provideLayoutRes(): Int = R.layout.activity_coin_list

    override fun provideViewModel(): CoinListViewModel =
        ViewModelProvider(this).get(CoinListViewModel::class.java)

    override fun provideNavigator(): CoinListNavigator = object : CoinListNavigator {}

    private fun updateList(list: List<SectionData<CoinListModel>>) {
        lifecycleScope.launch {
            var offset = 0
            val decoratorData: List<DecoratorData> =
                list.map { sectionData: SectionData<CoinListModel> ->
                    DecoratorData(
                        offset,
                        bindString(this@CoinListActivity, sectionData.sectionTitleRes)
                    ).also { offset += sectionData.sectionData.size }
                }
            adapter.applyChanges(list.flatMap { it.sectionData })
            withContext(Dispatchers.Main) {
                textDecorator.setData(decoratorData)
                coinListRecycler.invalidateItemDecorations()
            }
        }
    }
}