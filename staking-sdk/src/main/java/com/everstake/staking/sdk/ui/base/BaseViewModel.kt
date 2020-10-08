package com.everstake.staking.sdk.ui.base

import androidx.lifecycle.ViewModel
import java.lang.ref.WeakReference

/**
 * created by Alex Ivanov on 07.10.2020.
 */
internal open class BaseViewModel<N : BaseNavigator> : ViewModel() {
    private var navigatorRef: WeakReference<N?> = WeakReference(null)

    var navigator: N?
        get() = navigatorRef.get()
        set(value) {
            navigatorRef = WeakReference(value)
        }
}