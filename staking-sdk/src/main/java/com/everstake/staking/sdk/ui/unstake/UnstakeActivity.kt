package com.everstake.staking.sdk.ui.unstake

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.everstake.staking.sdk.R
import com.everstake.staking.sdk.data.Constants
import com.everstake.staking.sdk.data.model.ui.UnstakeModel
import com.everstake.staking.sdk.ui.base.BaseActivity
import com.everstake.staking.sdk.util.bindColor
import com.everstake.staking.sdk.util.bindString
import com.everstake.staking.sdk.util.formatTimeSeconds
import com.everstake.staking.sdk.util.getDataInfoSpan
import kotlinx.android.synthetic.main.activity_unstake.*
import kotlinx.android.synthetic.main.view_amount_input.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.math.BigDecimal
import java.math.BigInteger

/**
 * created by Alex Ivanov on 30.10.2020.
 */
@FlowPreview
@ExperimentalCoroutinesApi
internal class UnstakeActivity : BaseActivity<UnstakeViewModel>() {

    companion object {
        private const val KEY_COIN_ID = "Unstake.CoinId"

        fun getIntent(context: Context, coinId: String): Intent =
            Intent(context, UnstakeActivity::class.java).putExtra(KEY_COIN_ID, coinId)

        private fun getCoinId(intent: Intent): String? = intent.getStringExtra(KEY_COIN_ID)
    }

    private var textWatcher: TextWatcher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val coinId: String = getCoinId(intent) ?: return Unit.also { finish() }
        viewModel.setCoinId(coinId)

        setSupportActionBar(unstakeToolbar)
        unstakeAmountSeekBar.max = Constants.PROGRESS_MAX_VALUE
        unstakeButton.setOnClickListener {
            Toast.makeText(this, "Call app callback", Toast.LENGTH_SHORT).show()
        }
        textWatcher = inputAmount.doOnTextChanged { text, _, _, _ ->
            viewModel.updateAmount(text?.toString() ?: "")
        }
        unstakeAmountSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (!fromUser) return
                val progressScale: BigDecimal =
                    (progress.toDouble() / Constants.PROGRESS_MAX_VALUE).toBigDecimal()
                viewModel.updateProgress(progressScale)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        viewModel.unstakeModel.observe(this) { updateUI(it) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> onBackPressed().let { true }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        inputAmount.removeTextChangedListener(textWatcher)
        textWatcher = null
    }

    override fun provideLayoutRes(): Int = R.layout.activity_unstake

    override fun provideViewModel(): UnstakeViewModel =
        ViewModelProvider(this).get(UnstakeViewModel::class.java)

    private fun updateUI(model: UnstakeModel) {
        val (balance: String, amount: String, symbol: String, progress: BigDecimal, unstakeTimeSeconds: BigInteger) = model

        unstakeStakedAmount.text = getDataInfoSpan(
            info = bindString(this, R.string.unstake_staked),
            data = balance,
            dataColor = bindColor(this, R.color.everstakeTextColorPrimary)
        )
        if (inputAmount.text.toString() != amount) inputAmount.setText(amount)
        inputAmountSymbol.text = symbol

        val progressInt: Int = (progress * Constants.PROGRESS_MAX_VALUE.toBigDecimal()).toInt()
        if (unstakeAmountSeekBar.progress != progressInt) {
            unstakeAmountSeekBar.progress = progressInt
        }

        unstakeTimeInfo.text = if (unstakeTimeSeconds > BigInteger.ZERO) {
            bindString(
                this,
                R.string.unstake_time_format,
                formatTimeSeconds(this, unstakeTimeSeconds.toLong())
            )
        } else {
            bindString(this, R.string.unstake_time_immediate)
        }
    }
}