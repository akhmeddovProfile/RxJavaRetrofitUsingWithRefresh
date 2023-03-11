package com.example.rxjavaandretrofitusing.model

data class RequestForApi(
    val field1:Int,//send to Integer tip
    val field2:String,
    val field3:Boolean,//for chechk
    val field4:ArrayList<String>,//For RadioButton
    val field5:String //Image
)