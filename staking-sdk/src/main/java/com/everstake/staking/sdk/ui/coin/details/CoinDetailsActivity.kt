package com.everstake.staking.sdk.ui.coin.details

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.everstake.staking.sdk.R
import com.everstake.staking.sdk.data.model.ui.CoinDetailsModel
import com.everstake.staking.sdk.ui.base.BaseActivity
import com.everstake.staking.sdk.ui.calculator.CalculatorActivity
import com.everstake.staking.sdk.util.bindColor
import com.everstake.staking.sdk.util.bindString
import com.everstake.staking.sdk.util.getDataInfoSpan
import kotlinx.android.synthetic.main.activity_coin_details.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

/**
 * created by Alex Ivanov on 16.10.2020.
 */
@FlowPreview
@ExperimentalCoroutinesApi
internal class CoinDetailsActivity : BaseActivity<CoinDetailsViewModel>() {

    companion object {
        private const val KEY_COIN_ID = "CoinDetails.CoinId"

        fun getIntent(context: Context, coinId: String): Intent =
            Intent(context, CoinDetailsActivity::class.java).putExtra(KEY_COIN_ID, coinId)

        private fun getCoinId(intent: Intent): String? = intent.getStringExtra(KEY_COIN_ID)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(coinDetailsToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val intentCoinId: String = getCoinId(intent) ?: return Unit.also { finish() }
        viewModel.setCoinId(intentCoinId)

        viewModel.coinDetails.observe(this) { updateUI(it) }

        coinDetailsStakeButton.setOnClickListener {
            Toast.makeText(this, "Not implemented", Toast.LENGTH_SHORT).show()
        }
        coinDetailsCalculatorButton.setOnClickListener {
            val coinId: String = viewModel.coinDetails.value?.id ?: return@setOnClickListener
            startActivity(CalculatorActivity.getIntent(this, coinId))
        }
        coinDetailsUnstakeButton.setOnClickListener {
            Toast.makeText(this, "Not implemented", Toast.LENGTH_SHORT).show()
        }
        coinDetailsStakeClaimButton.setOnClickListener {
            Toast.makeText(this, "Not implemented", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.coin_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> onBackPressed().let { true }
        R.id.menu_info -> {
            viewModel.coinDetails.value?.aboutUrl
                ?.let { Uri.parse(it) }
                ?.also { url: Uri ->
                    CustomTabsIntent.Builder().build().launchUrl(this, url)
                }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun provideLayoutRes(): Int = R.layout.activity_coin_details

    override fun provideViewModel(): CoinDetailsViewModel =
        ViewModelProvider(this).get(CoinDetailsViewModel::class.java)

    private fun updateUI(coinDetails: CoinDetailsModel) {
        val (
            _: String,
            displayName: String,
            iconUrl: String,
            about: String,
            _: String,
            apr: String,
            serviceFee: String,
            showStakedSection: Boolean,
            stakedAmount: String,
            validatorName: String,
            yearlyIncome: String,
            showClaimSection: Boolean,
            availableToClaim: String) = coinDetails

        coinDetailsCoinTitle.text = displayName
        Glide.with(this).load(iconUrl).into(coinDetailsCoinImage)

        @ColorInt
        val headerSpanColor: Int = bindColor(this, R.color.everstakeTextColorPrimaryInverted)

        coinDetailsAPR.text = getDataInfoSpan(
            bindString(this, R.string.coin_details_apr),
            apr,
            headerSpanColor
        )
        coinDetailsServiceFee.text = getDataInfoSpan(
            bindString(this, R.string.coin_details_service_fee),
            serviceFee,
            headerSpanColor
        )

        coinDetailsStakedGroup.visibility =
            if (showStakedSection) View.VISIBLE else View.GONE
        coinDetailsStakedAmount.text = stakedAmount

        @ColorInt
        val stakedSpanColor: Int = bindColor(this, R.color.everstakeTextColorPrimary)

        coinDetailsStakedValidator.text = getDataInfoSpan(
            bindString(this, R.string.coin_details_validator),
            validatorName,
            stakedSpanColor
        )
        coinDetailsStakeYearProfit.text = getDataInfoSpan(
            bindString(this, R.string.coin_details_income),
            yearlyIncome,
            stakedSpanColor
        )

        coinDetailsClaimGroup.visibility = if (showClaimSection) View.VISIBLE else View.GONE

        coinDetailsAvailableClaim.text = getDataInfoSpan(
            bindString(this, R.string.coin_details_available_rewards),
            availableToClaim,
            stakedSpanColor
        )

        coinDetailsAboutText.text = about
    }
}