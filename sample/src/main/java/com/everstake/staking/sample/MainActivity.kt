package com.everstake.staking.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.everstake.staking.sdk.EverstakeStaking
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStaking.setOnClickListener { EverstakeStaking.launchStaking(this) }
    }
}