package com.vnamashko.transformers.network.service

import com.google.gson.GsonBuilder
import com.vnamashko.transformers.core.LocalStorage
import com.vnamashko.transformers.network.AuthInterceptor
import com.vnamashko.transformers.network.TransformerDeserializer
import com.vnamashko.transformers.network.TransformerSerializer
import com.vnamashko.transformers.network.model.Transformer
import com.vnamashko.transformers.network.model.Transformers
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

/**
 * @author Vlad Namashko
 */
interface ApiService {

    @GET("allspark")
    fun getToken(): Single<String>

    @GET("transformers")
    fun getAll(): Single<Transformers>

    @POST("transformers")
    fun add(@Body transformer: Transformer): Single<Transformer>

    @PUT("transformers")
    fun update(@Body transformer: Transformer): Single<Transformer>

    @DELETE("transformers/{transformerId}")
    fun delete(@Path("transformerId") id: String): Completable

    /**
     * Companion object to create the Service
     */
    companion object Factory {

        private const val BASE_URL = "https://transformers-api.firebaseapp.com/"

        fun create(storage: LocalStorage): ApiService {

            val builder = OkHttpClient.Builder().addInterceptor(AuthInterceptor(storage))

            val gson = GsonBuilder().setLenient()
                .registerTypeAdapter(Transformer::class.java, TransformerDeserializer())
                .registerTypeAdapter(Transformer::class.java, TransformerSerializer())
                .create()

            val retrofit = Retrofit.Builder()
                .client(builder.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }

}