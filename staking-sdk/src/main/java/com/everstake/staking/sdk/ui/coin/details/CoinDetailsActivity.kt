package com.everstake.staking.sdk.ui.coin.details

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabsIntent
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
import com.everstake.staking.sdk.ui.coin.details.epoxy.*
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

        val showList: Boolean =
            validators.size > 1 && stakeType == StakeType.MultiStakeToOneValidator

        val unstakeClick: (idsList: List<String>) -> Unit = { idsList: List<String> ->
            startActivity(
                UnstakeActivity.getIntent(
                    this@CoinDetailsActivity,
                    coinId,
                    idsList
                )
            )
        }
        val claimClick: () -> Unit = {
            EverstakeStaking.appCallback.get()?.onAction(
                EverstakeAction.CLAIM,
                coinDetails.coinSymbol,
                coinDetails.claimAmount,
                validators.map {
                    ValidatorInfo(
                        it.validatorName,
                        it.validatorAddress
                    )
                }
            )
        }

        coinDetailsRecycler.withModels {
            if (showStakedSection) {
                stakedHeaderView {
                    id("stakedHeader")
                    showList(showList)
                }
                if (showList) {
                    validators.forEach {
                        singleStakeView {
                            id(it.validatorId)
                            validator(it)
                            unstakeClickListener(unstakeClick)
                        }
                    }
                } else {
                    stakeView {
                        id("staked")
                        totalStakedAmount(totalStakedAmount)
                        validators(validators)
                        unstakeClickListener(unstakeClick)
                    }
                }
                if (showClaimSection) {
                    claimView {
                        id("claim")
                        claimAmount(claimAmount)
                        coinSymbol(coinSymbol)
                        claimClickListener(claimClick)
                    }
                }
            }
            aboutView {
                id("about")
                about(about)
            }
        }
    }
}