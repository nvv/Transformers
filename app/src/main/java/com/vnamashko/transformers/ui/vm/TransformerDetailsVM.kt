package com.vnamashko.transformers.ui.vm

import android.arch.lifecycle.ViewModel
import com.vnamashko.transformers.network.model.Transformer

/**
 * @author Vlad Namashko
 */
class TransformerDetailsVM(transformerArg: Transformer?) : ViewModel() {

    val state: Int = if (transformerArg?.id != null) STATE_EDIT else STATE_CREATE

    var transformer = transformerArg ?: Transformer()

    val isEdit = state == STATE_EDIT

    companion object {
        const val STATE_CREATE = 0
        const val STATE_EDIT = 1
    }

}