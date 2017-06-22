package com.yy.pillar.service

import com.google.gson.Gson
import com.yy.pillar.PillarApplication
import io.reactivex.schedulers.Schedulers
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Created by huangfan on 2017/6/21.
 */

class HttpService private constructor(){

    companion object{
        fun getInstance() : HttpService{
            return default
        }

        @JvmStatic private var default: HttpService = HttpService()
            private set
            get() = field
    }

    private var mGson : Gson = Gson()
    private var mOkHttpClient : OkHttpClient

    init {
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val cacheFile = File(PillarApplication.getContext().cacheDir, "cache")
        val cache = Cache(cacheFile, (1024 * 1024 * 100).toLong())

        mOkHttpClient = OkHttpClient.Builder()
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .addInterceptor(logInterceptor)
                .cache(cache)
                .build()
    }

    fun client(okHttpClient: OkHttpClient): HttpService {
        mOkHttpClient = okHttpClient
        return this
    }

    fun <TService> create(serviceClass: Class<out TService>): TService {
        return create(serviceClass, GsonConverterFactory.create(mGson))
    }

    fun <TService> create(serviceClass: Class<out TService>, converter: Converter.Factory): TService {
        val retrofit = Retrofit.Builder().client(mOkHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(converter)
                .build()
        return retrofit.create(serviceClass)
    }
}
