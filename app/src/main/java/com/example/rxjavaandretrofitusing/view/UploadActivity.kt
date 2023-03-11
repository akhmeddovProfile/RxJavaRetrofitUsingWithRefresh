package com.example.rxjavaandretrofitusing.view

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.example.rxjavaandretrofitusing.R
import com.example.rxjavaandretrofitusing.api.ApiForProject
import com.example.rxjavaandretrofitusing.model.DefaultResponse
import com.example.rxjavaandretrofitusing.model.RequestForApi
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.petsgo.android.services.RetrofitService2
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_upload.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class UploadActivity : AppCompatActivity() {

    var booleanforcheecking = false
    val BASE_URL = "https://us-central1-petsgo-9d7d6.cloudfunctions.net/app/api/"
    var compositeDisposable = CompositeDisposable()
    var radioButtonClicked = ArrayList<String>()
    private lateinit var storage: FirebaseStorage
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var firestore:FirebaseFirestore
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedBitmap: Bitmap? = null
    var selectedPicture: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        firestore=Firebase.firestore
        storage = Firebase.storage
        setContentView(R.layout.activity_upload)

        radioButtonClicked.clear()
        val check = checkBox
        check.setOnClickListener {
            if (checkBox.isChecked) {
                booleanforcheecking = true
                println("if"+booleanforcheecking)
            } else {
                booleanforcheecking = false
                println("else"+booleanforcheecking)
            }
        }
        registerLauncher()
        savebtn.setOnClickListener {
            postItem()
        }

    }

    fun postItem() {
        val age = editInt.text.toString().toInt()
        val name = editString.text.toString()

        val uuid = UUID.randomUUID() // random reqem sekiller qarismasin ve silinmesin
        val imageName = "$uuid.jpg"
        val reference = storage.reference

        radioButtonClicked.clear()
        if (radioButton.isChecked) {
            radioButtonClicked.add(radioButton.text.toString())
        }

        if (radioButton2.isChecked) {
            radioButtonClicked.add(radioButton2.text.toString())
        }

        if (radioButton3.isChecked) {
            radioButtonClicked.add(radioButton3.text.toString())
        }

        if (radioButton4.isChecked) {
            radioButtonClicked.add(radioButton4.text.toString())
        }


        println(booleanforcheecking)

        val imagesReference = reference.child("eminimages").child(imageName)
        imagesReference.putFile(selectedPicture!!).addOnSuccessListener { taskSnapshot ->
            val uploadedPictureReference =
                storage.reference.child("eminimages").child(imageName)
            uploadedPictureReference.downloadUrl.addOnSuccessListener { uri ->
                val downloadUrl = uri.toString()
                val imagePet = downloadUrl

                compositeDisposable = CompositeDisposable()
                val retrofit = RetrofitService2(BASE_URL).retrofit.create(
                    ApiForProject::class.java
                )
                println("checkbox"+booleanforcheecking)
                println("radiobutton"+radioButtonClicked)
                val request = RequestForApi(
                    age,
                    name,
                    booleanforcheecking,
                    radioButtonClicked,
                    imagePet
                )
                compositeDisposable.add(
                    retrofit.createAPI(request)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleResponseUserOrder, {
                            println(it)
                        })
                )
                finish()
            }
                .addOnFailureListener {
                    Toast.makeText(this@UploadActivity, it.message, Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun handleResponseUserOrder(response: DefaultResponse) {
       Toast.makeText(this@UploadActivity,response.message,Toast.LENGTH_SHORT).show()
    }

     fun setPicture(view: View) {

         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
             //sdk level 33
             if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                 //if  permission don't granted make snackbar
                 if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_MEDIA_IMAGES)){
                     Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",View.OnClickListener {
                         //request permission
                         permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                     }).show()
                     //request permission
                 }else{
                     permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                 }
                 //permission granted
             }else{
                 val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                 activityResultLauncher.launch(intentToGallery)
             }
             //////////////////////////////////////////////////////////////////
         }else{
             //sdk level 33>
             if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                 //if  permission don't granted make snackbar
                 if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                     Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",View.OnClickListener {
                         //request permission
                         permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                     }).show()
                     //request permission
                 }else{
                     permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                 }
                 //permission granted
             }else{
                 val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                 activityResultLauncher.launch(intentToGallery)
             }
         }
    }

    private fun registerLauncher() {
        println("--- P6 ---")
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            println("--- P7 ---")
            if (result.resultCode == RESULT_OK) {
                println("--- P8 ---")
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    println("--- P9 ---")
                    selectedPicture = intentFromResult.data
                    try {
                        println("--- P10 ---")
                        if (Build.VERSION.SDK_INT >= 28) {
                            println("--- P11 ---")
                            val source = ImageDecoder.createSource(
                                this@UploadActivity.contentResolver,
                                selectedPicture!!
                            )
                            selectedBitmap = ImageDecoder.decodeBitmap(source)
                            val newBitmap = BitmapFactory.decodeStream(
                                getContentResolver().openInputStream(selectedPicture!!)
                            )
                            imageView.setImageBitmap(newBitmap)
                        } else {
                            println("--- P12 ---")

                            selectedBitmap = MediaStore.Images.Media.getBitmap(
                                this@UploadActivity.contentResolver,
                                selectedPicture
                            )
                            imageView.setImageBitmap(selectedBitmap)
                        }
                    } catch (e: IOException) {
                        println("--- P13 ---")
                        e.printStackTrace()
                    }
                }
            }
        }
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result ->
            if (result) {
                //permission granted
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                //permission denied
                Toast.makeText(this@UploadActivity, "Permission needed!", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

}
