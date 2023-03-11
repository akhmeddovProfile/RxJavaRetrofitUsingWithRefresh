package com.petsgo.android.services

import com.google.gson.GsonBuilder
import com.petsgo.kbgstore.services.OkhttpService
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitService2(url:String) {
    private val client = OkhttpService().httpClient.build()
    val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(client)
        .build()
}
