package com.vnamashko.transformers.di

import com.vnamashko.transformers.MainActivity
import com.vnamashko.transformers.ui.TransformerDetailsFragment
import com.vnamashko.transformers.ui.TransformerListFragment
import dagger.Component
import javax.inject.Singleton

/**
 * @author Vlad Namashko
 */
@Singleton
@Component(modules = [(CoreModule::class)])
interface CoreComponent {

    fun inject(activity: MainActivity)

    fun inject(detailsFragment: TransformerDetailsFragment)

    fun inject(listFragment: TransformerListFragment)

}