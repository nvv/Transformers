package com.vnamashko.transformers.ui

import android.support.v4.app.Fragment
import io.reactivex.disposables.CompositeDisposable


/**
 * @author Vlad Namashko
 */
abstract class BaseFragment: Fragment() {

    protected val disposable = CompositeDisposable()

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    abstract fun getTitle(): String

    abstract fun hasBackNavigation(): Boolean

    abstract fun hasToolbar(): Boolean

    abstract fun canChangeStatusBarColor(): Boolean
}