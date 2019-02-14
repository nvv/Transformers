package com.vnamashko.transformers.core

import android.content.Context
import com.vnamashko.transformers.di.CoreComponent
import com.vnamashko.transformers.di.CoreModule
import com.vnamashko.transformers.di.DaggerCoreComponent
import com.vnamashko.transformers.utils.ViewUtils
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author Vlad Namashko
 */
class DependencyLocator private constructor(val context: Context) {

    private val coreModule: CoreModule = CoreModule(context)
    val coreComponent: CoreComponent = DaggerCoreComponent.builder().coreModule(coreModule).build()

    init {
        ViewUtils.init(context)
    }

    companion object {

        @Volatile private lateinit var INSTANCE: DependencyLocator
        private val initialized = AtomicBoolean()

        fun initInstance(context: Context) {
            if (!initialized.getAndSet(true)) {
                INSTANCE = buildDependencyLocator(context)
            }
        }

        fun getInstance() = INSTANCE

        private fun buildDependencyLocator(context: Context) = DependencyLocator(context)

    }
}