package com.vnamashko.transformers.di

import android.content.Context
import com.vnamashko.transformers.core.Storage
import com.vnamashko.transformers.network.service.ApiService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author Vlad Namashko
 */
@Module
class CoreModule(context: Context) {

    private val localStorage = Storage(context)

    @Provides
    @Singleton
    fun provideLocalStorage() = localStorage

    @Provides
    @Singleton
    fun provideApiService() = ApiService.create(localStorage)
}