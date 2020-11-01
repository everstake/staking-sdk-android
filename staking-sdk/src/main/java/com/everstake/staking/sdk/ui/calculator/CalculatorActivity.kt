package com.everstake.staking.sdk.ui.calculator

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextWatcher
import android.view.MenuItem
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.everstake.staking.sdk.R
import com.everstake.staking.sdk.data.model.ui.CalculatorModel
import com.everstake.staking.sdk.ui.base.BaseActivity
import com.everstake.staking.sdk.ui.coin.select.CoinSelectActivity
import com.everstake.staking.sdk.ui.stake.StakeActivity
import com.everstake.staking.sdk.ui.validator.select.ValidatorSelectActivity
import com.everstake.staking.sdk.util.bindString
import com.everstake.staking.sdk.util.setSelectableItemBackground
import kotlinx.android.synthetic.main.activity_calculator.*
import kotlinx.android.synthetic.main.view_amount_input.*
import kotlinx.android.synthetic.main.view_income_summary.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

/**
 * created by Alex Ivanov on 19.10.2020.
 */
@FlowPreview
@ExperimentalCoroutinesApi
internal class CalculatorActivity : BaseActivity<CalculatorViewModel>() {

    companion object {
        private const val CODE_COIN_SELECT: Int = 131
        private const val CODE_VALIDATOR_SELECT: Int = 132

        private const val KEY_CALC_COIN = "Calculator.CoinId"

        fun getIntent(context: Context, coinId: String): Intent =
            Intent(context, CalculatorActivity::class.java)
                .putExtra(KEY_CALC_COIN, coinId)

        fun getCoinId(intent: Intent): String? = intent.getStringExtra(KEY_CALC_COIN)
    }

    private var amountTextWatcher: TextWatcher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val coinId: String = getCoinId(intent) ?: return Unit.also { finish() }
        viewModel.updateCoinId(coinId)

        setSupportActionBar(calculatorToolbar)
        calculatorSelectCurrencyBg.setOnClickListener {
            startActivityForResult(CoinSelectActivity.getIntent(this), CODE_COIN_SELECT)
        }
        calculatorSelectValidatorBg.setOnClickListener {
            startActivityForResult(
                ValidatorSelectActivity.getIntent(
                    this,
                    viewModel.getCoinId(),
                    viewModel.getValidatorId()
                ), CODE_VALIDATOR_SELECT
            )
        }

        viewModel.calculatorData.observe(this) { updateUI(it) }

        this.amountTextWatcher = inputAmount.doOnTextChanged { text, _, _, _ ->
            viewModel.updateAmount(text.toString())
        }

        calculatorValidatorFeeCheckBox.setOnCheckedChangeListener { _, isChecked: Boolean ->
            viewModel.updateIncludeFee(isChecked)
        }
        calculatorReinvestCheckBox.setOnCheckedChangeListener { _, isChecked: Boolean ->
            viewModel.updateIncludeReinvest(isChecked)
        }

        calculatorStakeButton.setOnClickListener {
            startActivity(
                StakeActivity.getIntent(
                    this,
                    viewModel.getCoinId(),
                    viewModel.getValidatorId(),
                    inputAmount.text.toString()
                )
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> onBackPressed().let { true }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        when (requestCode) {
            CODE_COIN_SELECT -> viewModel.updateCoinId(
                data?.let { CoinSelectActivity.getCoinIdFromResult(it) } ?: return
            )
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

    override fun provideLayoutRes(): Int = R.layout.activity_calculator

    override fun provideViewModel(): CalculatorViewModel =
        ViewModelProvider(this).get(CalculatorViewModel::class.java)

    private fun updateUI(calculatorData: CalculatorModel) {
        inputAmountSymbol.text = calculatorData.coinSymbol

        calculatorCoinName.text = calculatorData.coinName
        calculatorCoinIncome.text = bindString(
            this,
            R.string.calculator_yearly_income,
            calculatorData.coinYearlyIncomePercent
        )

        calculatorSelectValidatorBg.apply {
            if (calculatorData.isReliableValidator) {
                setBackgroundResource(R.drawable.reliable_validator_bg)
            } else {
                setSelectableItemBackground()
            }
        }
        calculatorValidatorName.text = calculatorData.validatorName
        calculatorValidatorFee.text = bindString(
            this,
            R.string.common_fee_format,
            calculatorData.validatorFee
        )

        incomeDailyText.text = calculatorData.dailyIncome
        incomeMonthlyText.text = calculatorData.monthlyIncome
        incomeYearlyText.text = calculatorData.yearlyIncome
    }
}