package com.example.rxjavaandretrofitusing.api

import com.example.rxjavaandretrofitusing.model.DefaultResponse
import com.example.rxjavaandretrofitusing.model.GetTestResponse
import com.example.rxjavaandretrofitusing.model.RequestForApi
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.*

interface ApiForProject {

    //https://us-central1-petsgo-9d7d6.cloudfunctions.net/app/api/delete/
    //https://us-central1-petsgo-9d7d6.cloudfunctions.net/app/api/update/

    @POST("create/")
    fun createAPI(@Body requestForApi: RequestForApi):Observable<DefaultResponse>

    @GET ("get-data/")
    fun getTestData() : Observable<ArrayList<GetTestResponse>>

    @GET("get-data/{id}/")
    fun getDataById(@Path("id") id: String): Observable<GetTestResponse>

    @DELETE("delete/{id}")
    fun deleteItem(@Path("id") itemId: String): Observable<GetTestResponse>

    @PUT("update/{id}")
    fun updateItem(@Path("id") id: String, @Body testData: RequestForApi): Observable<RequestForApi>
}