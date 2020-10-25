package com.everstake.staking.sdk.ui.calculator

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.everstake.staking.sdk.R
import com.everstake.staking.sdk.ui.base.BaseActivity
import com.everstake.staking.sdk.ui.coin.select.CoinSelectActivity
import com.everstake.staking.sdk.ui.validator.select.ValidatorSelectActivity
import kotlinx.android.synthetic.main.activity_calculator.*

/**
 * created by Alex Ivanov on 19.10.2020.
 */
internal class CalculatorActivity : BaseActivity<CalculatorViewModel>() {

    companion object {
        private const val KEY_CALC_COIN = "Calculator.CoinId"

        fun getIntent(context: Context, coinId: String): Intent =
            Intent(context, CalculatorActivity::class.java)
                .putExtra(KEY_CALC_COIN, coinId)

        fun getCoinId(intent: Intent): String? = intent.getStringExtra(KEY_CALC_COIN)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val coinId: String = getCoinId(intent) ?: return Unit.also { finish() }

        calculatorSelectCurrencyBg.setOnClickListener {
            startActivity(CoinSelectActivity.getIntent(this))
        }
        calculatorSelectValidatorBg.setOnClickListener {
            startActivity(ValidatorSelectActivity.getIntent(this, coinId, "0"))
        }
    }

    override fun provideLayoutRes(): Int = R.layout.activity_calculator

    override fun provideViewModel(): CalculatorViewModel =
        ViewModelProvider(this).get(CalculatorViewModel::class.java)
}