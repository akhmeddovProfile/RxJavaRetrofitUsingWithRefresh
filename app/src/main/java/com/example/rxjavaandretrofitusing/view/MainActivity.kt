package com.example.rxjavaandretrofitusing.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rxjavaandretrofitusing.R
import com.example.rxjavaandretrofitusing.adapter.TestAdapter
import com.example.rxjavaandretrofitusing.api.ApiForProject
import com.example.rxjavaandretrofitusing.model.GetTestResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.petsgo.android.services.RetrofitService2
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_test_detail.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(),   TestAdapter.Listener{

    private lateinit var testAdapter: TestAdapter
    private lateinit var testList : ArrayList<GetTestResponse>
    private lateinit var testlist2 : ArrayList<GetTestResponse>
    private lateinit var compositeDisposable: CompositeDisposable
    private val BASE_URL = "https://us-central1-petsgo-9d7d6.cloudfunctions.net/app/api/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener {
            val intent = Intent(this@MainActivity,UploadActivity::class.java)
            startActivity(intent)
        }

        testList = ArrayList<GetTestResponse>()
        testlist2 = ArrayList<GetTestResponse>()
        recylerviewTest.layoutManager = GridLayoutManager(this,2)

        getTests()
        swiperefresh()
    }

    private fun swiperefresh(){
        swipetoRefresh.setOnRefreshListener {
            Toast.makeText(this@MainActivity,"Page is Refreshing",Toast.LENGTH_SHORT).show()
            testList.clear()
            testlist2.clear()
            getTests()
            swipetoRefresh.isRefreshing=false
        }
    }

    private fun getTests(){
        compositeDisposable = CompositeDisposable()
        val retrofit = RetrofitService2(BASE_URL).retrofit.create(ApiForProject::class.java)
        compositeDisposable.add(retrofit.getTestData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponse, { throwable-> println("MyTests: $throwable") }))
    }

    private fun handleResponse(response : ArrayList<GetTestResponse>){
        testList.addAll(response)
        testlist2.addAll(response)
        testAdapter = TestAdapter(testlist2, this)
        recylerviewTest.adapter = testAdapter
        testAdapter.notifyDataSetChanged()
    }

    override fun onTestItemClick(test: GetTestResponse) {
        println(test.field2)
        val intent = Intent(this, TestDetailActivity::class.java)
        intent.putExtra("id",test.id)
        intent.putExtra("image",test.field5)
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("myId", test.id)
        editor.apply()
        startActivity(intent)
        println(test.id)
    }
}