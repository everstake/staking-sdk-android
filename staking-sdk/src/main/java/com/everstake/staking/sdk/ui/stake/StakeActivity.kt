package com.everstake.staking.sdk.ui.stake

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import androidx.annotation.ColorInt
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.everstake.staking.sdk.EverstakeAction
import com.everstake.staking.sdk.EverstakeStaking
import com.everstake.staking.sdk.R
import com.everstake.staking.sdk.ValidatorInfo
import com.everstake.staking.sdk.data.Constants
import com.everstake.staking.sdk.data.model.ui.StakeModel
import com.everstake.staking.sdk.data.model.ui.StakeValidatorInfo
import com.everstake.staking.sdk.ui.base.BaseActivity
import com.everstake.staking.sdk.ui.validator.select.ValidatorSelectActivity
import com.everstake.staking.sdk.util.bindColor
import com.everstake.staking.sdk.util.bindString
import com.everstake.staking.sdk.util.getDataInfoSpan
import com.everstake.staking.sdk.util.setSelectableItemBackground
import kotlinx.android.synthetic.main.activity_stake.*
import kotlinx.android.synthetic.main.view_amount_input.*
import kotlinx.android.synthetic.main.view_income_summary.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.math.BigDecimal

/**
 * created by Alex Ivanov on 01.11.2020.
 */
@FlowPreview
@ExperimentalCoroutinesApi
internal class StakeActivity : BaseActivity<StakeViewModel>() {

    companion object {
        private const val KEY_COIN_ID = "Stake.CoinId"
        private const val KEY_VALIDATORS = "Stake.Validators"
        private const val KEY_AMOUNT = "Stake.Amount"

        private const val CODE_VALIDATOR_SELECT = 1612

        fun getIntent(
            context: Context,
            coinId: String,
            validators: List<String> = emptyList(),
            amount: String? = null
        ): Intent =
            Intent(context, StakeActivity::class.java)
                .putExtra(KEY_COIN_ID, coinId)
                .putStringArrayListExtra(KEY_VALIDATORS, ArrayList(validators))
                .putExtra(KEY_AMOUNT, amount)

        private fun getCoinId(intent: Intent): String? = intent.getStringExtra(KEY_COIN_ID)

        private fun getValidators(intent: Intent): List<String> =
            (intent.getStringArrayListExtra(KEY_VALIDATORS) ?: emptyList())
                .also { intent.removeExtra(KEY_VALIDATORS) }

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
                    viewModel.getSelectedValidator(),
                    viewModel.allowMultiValidator()
                ), CODE_VALIDATOR_SELECT
            )
        }
        this.amountTextWatcher = inputAmount.doOnTextChanged { text, _, _, _ ->
            viewModel.updateAmount(text.toString())
        }
        stakeAmountSeekBar.max = Constants.PROGRESS_MAX_VALUE
        stakeAmountSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (!fromUser) return
                val progressScale: BigDecimal =
                    (progress.toDouble() / Constants.PROGRESS_MAX_VALUE).toBigDecimal()
                viewModel.updateProgress(progressScale)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        stakeButton.setOnClickListener {
            val stakeModel: StakeModel = viewModel.stakeInfo.value ?: return@setOnClickListener
            val validators: List<ValidatorInfo> =
                stakeModel.validators.map { ValidatorInfo(it.validatorName, it.validatorAddress) }
            EverstakeStaking.appCallback.get()?.onAction(
                EverstakeAction.STAKE,
                stakeModel.coinSymbol,
                stakeModel.amount,
                validators
            )
        }

        viewModel.stakeInfo.observe(this) { updateUI(it) }

        getValidators(intent).also { viewModel.updateValidators(it) }
        getAmount(intent)?.also { updateAmountText(it) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> onBackPressed().let { true }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        when (requestCode) {
            CODE_VALIDATOR_SELECT -> viewModel.updateValidators(
                data?.let { ValidatorSelectActivity.getValidators(it) } ?: return
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

    private fun updateUI(stakeModel: StakeModel) {
        val (_: String,
            balance: String,
            amount: String,
            progress: BigDecimal,
            coinSymbol: String,
            coinYearlyIncomePercent: String,
            allowMultipleValidator: Boolean,
            validators: List<StakeValidatorInfo>,
            isReliableValidator: Boolean,
            dailyIncome: String,
            monthlyIncome: String,
            yearlyIncome: String) = stakeModel

        @ColorInt val spanColor: Int = bindColor(this, R.color.everstakeTextColorPrimary)

        stakeToolbar.subtitle = getDataInfoSpan(
            bindString(this, R.string.stake_subtitle),
            coinYearlyIncomePercent,
            spanColor
        )

        stakeBalance.text = getDataInfoSpan(
            bindString(this, R.string.stake_balance_label),
            balance,
            spanColor
        )

        updateAmountText(amount)
        inputAmountSymbol.text = coinSymbol

        val progressInt: Int = (progress * Constants.PROGRESS_MAX_VALUE.toBigDecimal()).toInt()
        if (stakeAmountSeekBar.progress != progressInt) {
            stakeAmountSeekBar.progress = progressInt
        }

        stakeSelectValidatorBg.apply {
            if (isReliableValidator) {
                setBackgroundResource(R.drawable.reliable_validator_bg)
            } else {
                setSelectableItemBackground()
            }
        }
        stakeValidatorName.text = validators.joinToString(", ") { it.validatorName }
        if (allowMultipleValidator) {
            stakeValidatorFee.visibility = View.GONE
        } else if (validators.isNotEmpty()) {
            stakeValidatorFee.visibility = View.VISIBLE
            stakeValidatorFee.text = bindString(
                this,
                R.string.common_fee_format,
                validators.first().validatorFee
            )
        }
        stakeValidatorReliableLabel.visibility =
            if (isReliableValidator) View.VISIBLE else View.GONE

        incomeDailyText.text = dailyIncome
        incomeMonthlyText.text = monthlyIncome
        incomeYearlyText.text = yearlyIncome
    }

    private fun updateAmountText(amount: String) {
        if (inputAmount.text.toString() != amount) {
            inputAmount.setText(amount)
            if (inputAmount.isFocused) inputAmount.setSelection(amount.length)
        }
    }
}