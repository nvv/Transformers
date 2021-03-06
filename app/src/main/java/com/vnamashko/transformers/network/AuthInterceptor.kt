package com.vnamashko.transformers.network

import com.vnamashko.transformers.core.Storage
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author Vlad Namashko
 */
class AuthInterceptor(private val storage: Storage) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()

        storage.token?.let {
            builder.addHeader(HEADER_AUTH, "Bearer $it")
        }
        builder.addHeader("Content-Type", "application/json")

        val request = builder.build()
        return chain.proceed(request)
    }

    companion object {
        private const val HEADER_AUTH = "Authorization"
    }

}