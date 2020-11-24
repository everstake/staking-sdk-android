package com.everstake.staking.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.everstake.staking.sdk.EverstakeAction
import com.everstake.staking.sdk.EverstakeBalanceModel
import com.everstake.staking.sdk.EverstakeListener
import com.everstake.staking.sdk.EverstakeStaking
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val stakeListener: EverstakeListener = object : EverstakeListener {
        override fun onAction(
            actionType: EverstakeAction,
            coinSymbol: String,
            amount: String,
            validatorName: String,
            validatorAddress: String
        ) {
            Log.d(
                "Everstake_Action",
                "ActionType = $actionType\tcoinSymbol = $coinSymbol\tamount = $amount\tvalidatorName = $validatorName\tvalidatorAddress = $validatorAddress˚"
            )
            val refreshMap: Map<String, String> = balances.map { it.coinSymbol to it.address }
                .toMap()
                .filter { it.key == coinSymbol }
            EverstakeStaking.refreshStaked(refreshMap)
        }
    }
    private val balances: List<EverstakeBalanceModel> = listOf(
        EverstakeBalanceModel("XTZ", "tz1LLNkQK4UQV6QcFShiXJ2vT2ELw449MzAA", "285974.184392"),
        EverstakeBalanceModel("ICX", "hx3e7ef35bfb8d65023dc25e79d9f9652d645a9b4f", "137.095"),
        EverstakeBalanceModel(
            "ATOM",
            "cosmos1gdmscydnyl0pj6lcjzmeuhr6g5g68u97z3jm8l",
            "15625306.963111"
        ),
        EverstakeBalanceModel("BAND", "band1gla2nv4alquzmga9xtgnfxsv50f8flzuxhm5ac", "0")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        EverstakeStaking.updateBalances(balances)

        btnStaking.setOnClickListener {
            EverstakeStaking.launchStaking(this, stakeListener)
            Log.d(
                "Everstake",
                "SupportedCoins: ${EverstakeStaking.getAvailableCoins().joinToString((", "))}"
            )
        }
    }
}