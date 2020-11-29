package com.everstake.staking.sdk.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * created by Alex Ivanov on 07.10.2020.
 */
internal abstract class BaseNavigatorActivity<VM : BaseNavigatorViewModel<N>, N : BaseNavigator> :
    AppCompatActivity() {
    protected lateinit var viewModel: VM
    protected lateinit var navigator: N

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(provideLayoutRes())
        viewModel = provideViewModel()
        navigator = provideNavigator()
        viewModel.navigator = navigator
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.navigator = null
    }

    abstract fun provideLayoutRes(): Int
    abstract fun provideViewModel(): VM
    abstract fun provideNavigator(): N
}

internal abstract class BaseActivity<VM : BaseViewModel> :
    BaseNavigatorActivity<VM, EmptyNavigator>() {
    override fun provideNavigator(): EmptyNavigator = EmptyNavigator
}