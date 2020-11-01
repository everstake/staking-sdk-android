package com.everstake.staking.sdk.ui.stake

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextWatcher
import android.view.MenuItem
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.everstake.staking.sdk.R
import com.everstake.staking.sdk.ui.base.BaseActivity
import com.everstake.staking.sdk.ui.validator.select.ValidatorSelectActivity
import kotlinx.android.synthetic.main.activity_stake.*
import kotlinx.android.synthetic.main.view_amount_input.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

/**
 * created by Alex Ivanov on 01.11.2020.
 */
@FlowPreview
@ExperimentalCoroutinesApi
internal class StakeActivity : BaseActivity<StakeViewModel>() {

    companion object {
        private const val KEY_COIN_ID = "Stake.CoinId"
        private const val KEY_VALIDATOR_ID = "Stake.ValidatorId"
        private const val KEY_AMOUNT = "Stake.Amount"

        private const val CODE_VALIDATOR_SELECT = 1612

        fun getIntent(
            context: Context,
            coinId: String,
            validatorId: String? = null,
            amount: String? = null
        ): Intent =
            Intent(context, StakeActivity::class.java)
                .putExtra(KEY_COIN_ID, coinId)
                .putExtra(KEY_VALIDATOR_ID, validatorId)
                .putExtra(KEY_AMOUNT, amount)

        private fun getCoinId(intent: Intent): String? = intent.getStringExtra(KEY_COIN_ID)

        private fun getValidatorId(intent: Intent): String? =
            intent.getStringExtra(KEY_VALIDATOR_ID)
                .also { intent.removeExtra(KEY_VALIDATOR_ID) }

        private fun getAmount(intent: Intent): String? = intent.getStringExtra(KEY_AMOUNT)
            .also { intent.removeExtra(KEY_AMOUNT) }
    }

    private var amountTextWatcher: TextWatcher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val coinId: String = getCoinId(intent) ?: return Unit.also { finish() }
        viewModel.updateCoinId(coinId)

        setSupportActionBar(stakeToolbar)
        stakeSelectValidatorBg.setOnClickListener {
            startActivityForResult(
                ValidatorSelectActivity.getIntent(
                    this,
                    viewModel.getCoinId(),
                    viewModel.getValidatorId()
                ), CODE_VALIDATOR_SELECT
            )
        }
        this.amountTextWatcher = inputAmount.doOnTextChanged { text, _, _, _ ->
            viewModel.updateAmount(text.toString())
        }

        getValidatorId(intent)?.also { viewModel.updateValidatorId(it) }
        getAmount(intent)?.also { inputAmount.setText(it) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> onBackPressed().let { true }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        when (requestCode) {
            CODE_VALIDATOR_SELECT -> viewModel.updateValidatorId(
                data?.let { ValidatorSelectActivity.getValidatorId(it) } ?: return
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        inputAmount.removeTextChangedListener(amountTextWatcher)
        amountTextWatcher = null
    }

    override fun provideLayoutRes(): Int = R.layout.activity_stake

    override fun provideViewModel(): StakeViewModel =
        ViewModelProvider(this).get(StakeViewModel::class.java)
}