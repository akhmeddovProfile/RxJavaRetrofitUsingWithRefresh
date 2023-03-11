package com.example.rxjavaandretrofitusing.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.rxjavaandretrofitusing.R
import com.example.rxjavaandretrofitusing.api.ApiForProject
import com.example.rxjavaandretrofitusing.model.GetTestResponse
import com.example.rxjavaandretrofitusing.model.RequestForApi
import com.petsgo.android.services.RetrofitService2
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_test_detail.*
import kotlinx.android.synthetic.main.activity_update.*
import kotlinx.android.synthetic.main.activity_upload.*

class UpdateActivity : AppCompatActivity() {
    var radioButtonClicked = ArrayList<String>()
    private lateinit var compositeDisposable: CompositeDisposable
    private val BASE_URL = "https://us-central1-petsgo-9d7d6.cloudfunctions.net/app/api/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
       val id = sharedPreferences.getString("myId","")
        println("updateId:" +id)
    }
}