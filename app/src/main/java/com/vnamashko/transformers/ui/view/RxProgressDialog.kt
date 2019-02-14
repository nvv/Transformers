package com.vnamashko.transformers.ui.view

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.support.annotation.StringRes
import com.vnamashko.transformers.R
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull

/**
 * Rx Helper that will show cancelable progress dialog and dispose subscription on cancellation
 *
 * @author Vlad Namashko
 */
class RxProgressDialog<U> : CompletableTransformer, SingleTransformer<U, U> {

    private val message: String
    private lateinit var context: Context
    private var dialog: Dialog? = null

    constructor(context: Context) {
        this.context = context
        message = this.context.getString(R.string.dlg_processing)
    }

    constructor(context: Context, @StringRes res: Int) {
        this.context = context
        message = this.context.getString(res)
    }


    override fun apply(@NonNull upstream: Completable): CompletableSource {
        return upstream.observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { disposable ->
                    dialog = showProgressDialog(message, DialogInterface.OnCancelListener { disposable.dispose() })
                }
                .doOnDispose { dismissDialog() }
                .doOnComplete { dismissDialog() }
                .doOnError { throwable -> dismissDialog() }
    }

    override fun apply(@NonNull upstream: Single<U>): Single<U> {
        return upstream.observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { disposable ->
                    dialog = showProgressDialog(context.getString(R.string.dlg_processing), DialogInterface.OnCancelListener { disposable.dispose() })
                }
                .doOnDispose { dismissDialog() }
                .doOnSuccess { u -> dismissDialog() }
                .doOnError { throwable -> dismissDialog() }
    }

    private fun dismissDialog() {
        try {
            dialog!!.dismiss()
        } catch (e: Throwable) {
            e.printStackTrace()
        }

    }

    fun showProgressDialog(title: CharSequence, cancelListener: DialogInterface.OnCancelListener?): ProgressDialog {
        try {
            val dialog = object : ProgressDialog(context, R.style.ProgressDialogStyle) {
                override fun dismiss() {
                    try {
                        super.dismiss()
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }

                }
            }
            dialog.setTitle(title)
            dialog.setMessage(message)
            dialog.isIndeterminate = true
            dialog.setCancelable(false)
            dialog.setOnCancelListener(cancelListener)
            dialog.show()

            return dialog
        } catch (e: Throwable) {
            e.printStackTrace()

            return object : ProgressDialog(context) {
                override fun dismiss() {
                    try {
                        super.dismiss()
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }

                }
            }
        }

    }
}