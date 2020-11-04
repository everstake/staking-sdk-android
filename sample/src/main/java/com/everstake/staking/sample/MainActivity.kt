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
            payload: Map<String, Any>
        ) {
            Log.d(
                "Everstake Action",
                "ActionType = $actionType\tcoinSymbol = $coinSymbol\tpayload = $payload˚"
            )
            val refreshMap: Map<String, String> = balances.map { it.coinSymbol to it.address }
                .toMap()
                .filter { it.key == coinSymbol }
            EverstakeStaking.refreshStaked(refreshMap)
        }

    }
    private val balances: List<EverstakeBalanceModel> = listOf(
        EverstakeBalanceModel("XTZ", "tz2B2xFMrTi1fHe7y94bMAFua6GPQM2XtTkV", "2099.8"),
        EverstakeBalanceModel("ICX", "cx299d88908ab371d586c8dfe0ed42899a899e6e5b", "41.43"),
        EverstakeBalanceModel(
            "ATOM",
            "cosmos1xxkueklal9vejv9unqu80w9vptyepfa95pd53u",
            "921587.707249"
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