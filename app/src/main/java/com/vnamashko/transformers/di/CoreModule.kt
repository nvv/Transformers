package com.vnamashko.transformers.di

import android.content.Context
import com.vnamashko.transformers.core.LocalStorage
import com.vnamashko.transformers.network.service.ApiService
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import javax.inject.Singleton

/**
 * @author Vlad Namashko
 */
@Module
class CoreModule(private val context: Context) {

    private val localStorage = LocalStorage(context)

    @Provides
    @Singleton
    fun provideLocalStorage() = localStorage

    @Provides
    @Singleton
    fun provideApiService() = ApiService.create(localStorage)
}