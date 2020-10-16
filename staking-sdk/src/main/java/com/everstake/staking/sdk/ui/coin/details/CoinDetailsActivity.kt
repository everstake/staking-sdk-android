package com.everstake.staking.sdk.ui.coin.details

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Spannable.SPAN_INCLUSIVE_EXCLUSIVE
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.everstake.staking.sdk.R
import com.everstake.staking.sdk.ui.base.BaseActivity
import com.everstake.staking.sdk.util.bindColor
import com.everstake.staking.sdk.util.bindString
import kotlinx.android.synthetic.main.activity_coin_details.*

/**
 * created by Alex Ivanov on 16.10.2020.
 */
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

        val coinId: String = getCoinId(intent) ?: return Unit.also { finish() }
        viewModel.setCoinId(coinId)

        viewModel.coinDetails.observe(this) { (
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
                                                  availableToClaim: String) ->
            coinDetailsCoinTitle.text = displayName
            Glide.with(this).load(iconUrl).into(coinDetailsCoinImage)

            @ColorInt
            val headerSpanColor: Int = bindColor(this, R.color.everstakeTextColorPrimaryInverted)

            coinDetailsAPR.text =
                SpannableStringBuilder(bindString(this, R.string.coin_details_apr))
                    .append(' ')
                    .append(
                        apr,
                        ForegroundColorSpan(headerSpanColor),
                        SPAN_INCLUSIVE_EXCLUSIVE
                    )

            coinDetailsServiceFee.text =
                SpannableStringBuilder(bindString(this, R.string.coin_details_service_fee))
                    .append(' ')
                    .append(
                        serviceFee,
                        ForegroundColorSpan(headerSpanColor),
                        SPAN_INCLUSIVE_EXCLUSIVE
                    )

            coinDetailsStakedContainer.visibility =
                if (showStakedSection) View.VISIBLE else View.GONE
            coinDetailsStakedAmount.text = stakedAmount

            @ColorInt
            val stakedSpanColor: Int = bindColor(this, R.color.everstakeTextColorPrimary)

            coinDetailsStakedValidator.text =
                SpannableStringBuilder(bindString(this, R.string.coin_details_validator))
                    .append(' ')
                    .append(
                        validatorName,
                        ForegroundColorSpan(stakedSpanColor),
                        SPAN_INCLUSIVE_EXCLUSIVE
                    )

            coinDetailsStakeYearProfit.text =
                SpannableStringBuilder(bindString(this, R.string.coin_details_income))
                    .append(' ')
                    .append(
                        yearlyIncome,
                        ForegroundColorSpan(stakedSpanColor),
                        SPAN_INCLUSIVE_EXCLUSIVE
                    )

            val claimVisibility: Int = if (showClaimSection) View.VISIBLE else View.GONE
            coinDetailsStakeClaimButton.visibility = claimVisibility
            coinDetailsAvailableClaim.visibility = claimVisibility

            coinDetailsAvailableClaim.text =
                SpannableStringBuilder(bindString(this, R.string.coin_details_available_rewards))
                    .append(' ')
                    .append(
                        availableToClaim,
                        ForegroundColorSpan(stakedSpanColor),
                        SPAN_INCLUSIVE_EXCLUSIVE
                    )

            coinDetailsAboutText.text = about
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
}