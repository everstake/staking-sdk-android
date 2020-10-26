package com.everstake.staking.sdk.ui.validator.select

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.everstake.staking.sdk.R
import com.everstake.staking.sdk.data.model.ui.ValidatorListModel
import com.everstake.staking.sdk.ui.base.BaseActivity
import com.everstake.staking.sdk.ui.base.list.RecyclerClickListener
import com.everstake.staking.sdk.ui.base.list.decorator.DividerDecorator
import com.everstake.staking.sdk.util.dpToPx
import kotlinx.android.synthetic.main.activity_validator_select.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch

/**
 * created by Alex Ivanov on 23.10.2020.
 */
@FlowPreview
@ExperimentalCoroutinesApi
internal class ValidatorSelectActivity : BaseActivity<ValidatorSelectViewModel>() {

    companion object {
        private const val KEY_COIN_ID = "SelectValidator.CoinId"
        private const val KEY_VALIDATOR_ID = "SelectValidator.ValidatorId"

        fun getIntent(context: Context, coinId: String, selectedValidator: String?): Intent =
            Intent(context, ValidatorSelectActivity::class.java)
                .putExtra(KEY_COIN_ID, coinId)
                .putExtra(KEY_VALIDATOR_ID, selectedValidator)

        fun getValidatorId(intent: Intent): String? = intent.getStringExtra(KEY_VALIDATOR_ID)

        private fun getResultIntent(validatorId: String): Intent =
            Intent().putExtra(KEY_VALIDATOR_ID, validatorId)

        private fun getCoinId(intent: Intent): String? = intent.getStringExtra(KEY_COIN_ID)
    }

    private val adapter: ValidatorSelectAdapter by lazy { ValidatorSelectAdapter() }
    private val decorator: RecyclerView.ItemDecoration by lazy {
        DividerDecorator(marginLeft = dpToPx(72))
    }
    private val clickListener: RecyclerClickListener<ValidatorListModel> =
        object : RecyclerClickListener<ValidatorListModel>() {
            override fun onClick(pos: Int, model: ValidatorListModel?) {
                model ?: return
                setResult(Activity.RESULT_OK, getResultIntent(model.id))
                onBackPressed()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val coinId: String = getCoinId(intent) ?: return Unit.also { finish() }
        val validatorId: String? = getValidatorId(intent)
        viewModel.initViewModel(coinId, validatorId)

        setSupportActionBar(selectValidatorToolbar)
        selectValidatorRecycler.addItemDecoration(decorator)
        selectValidatorRecycler.layoutManager = LinearLayoutManager(this)
        adapter.setClickListener(clickListener)
        selectValidatorRecycler.adapter = adapter

        viewModel.listData.observe(this) { validators: List<ValidatorListModel> ->
            lifecycleScope.launch { adapter.applyChanges(validators) }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> onBackPressed().let { true }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        selectValidatorRecycler.removeItemDecoration(decorator)
    }

    override fun provideLayoutRes(): Int = R.layout.activity_validator_select

    override fun provideViewModel(): ValidatorSelectViewModel =
        ViewModelProvider(this).get(ValidatorSelectViewModel::class.java)
}