package com.everstake.staking.sdk.ui.coin.details

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.everstake.staking.sdk.EverstakeAction
import com.everstake.staking.sdk.EverstakeStaking
import com.everstake.staking.sdk.R
import com.everstake.staking.sdk.ValidatorInfo
import com.everstake.staking.sdk.data.model.api.StakeType
import com.everstake.staking.sdk.data.model.ui.CoinDetailsModel
import com.everstake.staking.sdk.data.model.ui.CoinDetailsValidatorInfo
import com.everstake.staking.sdk.ui.base.BaseActivity
import com.everstake.staking.sdk.ui.calculator.CalculatorActivity
import com.everstake.staking.sdk.ui.stake.StakeActivity
import com.everstake.staking.sdk.ui.unstake.UnstakeActivity
import com.everstake.staking.sdk.util.bindColor
import com.everstake.staking.sdk.util.bindString
import com.everstake.staking.sdk.util.getDataInfoSpan
import kotlinx.android.synthetic.main.activity_coin_details.*
import kotlinx.android.synthetic.main.view_stake_detail.view.*
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
            val coinId: String = viewModel.getCoinId() ?: return@setOnClickListener
            startActivity(StakeActivity.getIntent(this, coinId))
        }
        coinDetailsCalculatorButton.setOnClickListener {
            val coinId: String = viewModel.getCoinId() ?: return@setOnClickListener
            startActivity(CalculatorActivity.getIntent(this, coinId))
        }

        coinDetailsStakeClaimButton.setOnClickListener {
            val coinDetails: CoinDetailsModel =
                viewModel.coinDetails.value ?: return@setOnClickListener
            EverstakeStaking.appCallback.get()?.onAction(
                EverstakeAction.CLAIM,
                coinDetails.coinSymbol,
                coinDetails.claimAmount,
                coinDetails.validators.map {
                    ValidatorInfo(it.validatorName, it.validatorAddress)
                }
            )
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
            coinId: String,
            coinName: String,
            coinSymbol: String,
            stakeType: StakeType,
            iconUrl: String,
            about: String,
            _: String,
            apr: String,
            serviceFee: String,
            showStakedSection: Boolean,
            totalStakedAmount: String,
            validators: List<CoinDetailsValidatorInfo>,
            showClaimSection: Boolean,
            claimAmount: String) = coinDetails

        val displayName = "$coinName ($coinSymbol)"
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

        @ColorInt
        val stakedSpanColor: Int = bindColor(this, R.color.everstakeTextColorPrimary)
        coinDetailsStakedGroup.visibility =
            if (showStakedSection) View.VISIBLE else View.GONE
        coinDetailsStakedContainer.apply {
            removeAllViews()
            when {
                validators.size > 1 && stakeType == StakeType.MultiStakeToOneValidator -> {
                    coinDetailsStakeHeader.text =
                        bindString(context, R.string.coin_details_staked_list)
                    stakeHeaderDivider.visibility = View.VISIBLE
                    validators.forEach { validatorInfo: CoinDetailsValidatorInfo ->
                        addView(layoutInflater.inflate(R.layout.view_stake_detail, this, false)
                            .apply {
                                coinDetailsUnstakeButton.setOnClickListener {
                                    startActivity(
                                        UnstakeActivity.getIntent(
                                            this@CoinDetailsActivity,
                                            coinId,
                                            listOf(validatorInfo.validatorId)
                                        )
                                    )
                                }
                                coinDetailsStakedAmount.text = validatorInfo.stakedAmount
                                coinDetailsStakedValidator.text = getDataInfoSpan(
                                    bindString(context, R.string.coin_details_validator),
                                    validatorInfo.validatorName,
                                    stakedSpanColor
                                )
                            })
                    }
                }
                validators.isNotEmpty() -> {
                    coinDetailsStakeHeader.text = bindString(context, R.string.coin_details_staked)
                    stakeHeaderDivider.visibility = View.GONE
                    addView(layoutInflater.inflate(R.layout.view_stake_detail, this, false).apply {
                        coinDetailsUnstakeButton.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                            topMargin = 0
                        }
                        coinDetailsUnstakeButton.setOnClickListener {
                            startActivity(
                                UnstakeActivity.getIntent(
                                    this@CoinDetailsActivity,
                                    coinId,
                                    validators.map { it.validatorId })
                            )
                        }
                        coinDetailsStakedAmount.text = totalStakedAmount
                        coinDetailsStakedValidator.text = getDataInfoSpan(
                            bindString(context, R.string.coin_details_validator),
                            validators.joinToString(", ") { it.validatorName },
                            stakedSpanColor
                        )
                    })
                }
            }
        }
//        coinDetailsStakedAmount.text = totalStakedAmount


//        coinDetailsStakedValidator.text = getDataInfoSpan(
//            bindString(this, R.string.coin_details_validator),
//            validators.joinToString(", ") { it.validatorName },
//            stakedSpanColor
//        )

        coinDetailsClaimGroup.visibility = if (showClaimSection) View.VISIBLE else View.GONE

        coinDetailsAvailableClaim.text = getDataInfoSpan(
            bindString(this, R.string.coin_details_available_rewards),
            "$claimAmount $coinSymbol",
            stakedSpanColor
        )

        coinDetailsAboutText.text = about
    }
}