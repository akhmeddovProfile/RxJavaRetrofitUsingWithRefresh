package com.example.rxjavaandretrofitusing.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.rxjavaandretrofitusing.R
import com.example.rxjavaandretrofitusing.adapter.TestAdapter
import com.example.rxjavaandretrofitusing.api.ApiForProject
import com.example.rxjavaandretrofitusing.constants.radioArgsName
import com.example.rxjavaandretrofitusing.model.GetTestResponse
import com.example.rxjavaandretrofitusing.model.RequestForApi
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.google.gson.annotations.SerializedName
import com.petsgo.android.services.RetrofitService2
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_test_detail.*
import kotlinx.android.synthetic.main.activity_upload.*
import retrofit2.HttpException
import java.io.IOException
import java.util.*


class TestDetailActivity : AppCompatActivity() {

    var radioButtonUpdateList = ArrayList<String>()
    var booleanForCheckUpdate = false
    private lateinit var compositeDisposable: CompositeDisposable
    private val BASE_URL = "https://us-central1-petsgo-9d7d6.cloudfunctions.net/app/api/"
    private lateinit var storage: FirebaseStorage
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var firestore: FirebaseFirestore
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedBitmap: Bitmap? = null
    var selectedPicture: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_detail)

        storage = Firebase.storage
        detailImage.isEnabled = false
        radioButtonDetail1.isEnabled = false
        radioButtonDetail2.isEnabled = false
        radioButtonDetail3.isEnabled = false
        radioButtonDetail4.isEnabled = false
        detailCheckbox.isEnabled = false
        val id = intent.getStringExtra("id")
        getDataFromServer(id!!)
        println(id)

        delete.setOnClickListener {
            val alert = AlertDialog.Builder(this)
            alert.setTitle("Delete")
            alert.setMessage("Are you sure?")
            alert.setPositiveButton("Yes") { dialog, which ->
                deleteItemFromServer(id)
                Toast.makeText(applicationContext, "delete", Toast.LENGTH_SHORT).show()
                finish()
            }
            alert.setNegativeButton("No") { dialog, which ->
                Toast.makeText(applicationContext, "not delete", Toast.LENGTH_SHORT).show()
            }
            alert.show()
        }

        update.setOnClickListener {
            radioButtonDetail1.isEnabled = true
            radioButtonDetail2.isEnabled = true
            radioButtonDetail3.isEnabled = true
            radioButtonDetail4.isEnabled = true
            detailCheckbox.isEnabled = true
            savechange.visibility = View.VISIBLE
            detailName.isEnabled = true
            detailAge.isEnabled = true
            detailImage.isEnabled = true
            titleupdate.visibility = View.VISIBLE
            delete.visibility = View.GONE
        }

        registerLauncher()

        savechange.setOnClickListener {
            updateItemFromServer(id)
        }
    }

    private fun getDataFromServer(id: String) {
        compositeDisposable = CompositeDisposable()
        val retrofit = RetrofitService2(BASE_URL).retrofit.create(ApiForProject::class.java)
        compositeDisposable.add(retrofit.getDataById(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponse,
                { throwable -> println("MyTests: $throwable") }
            ))
    }

    private fun handleResponse(response: GetTestResponse) {
        setUpRadioButton(response.field4)
        setUpCheckBox(response.field3)
        Picasso.get().load(response.field5).into(detailImage)
        val name = response.field2
        val age = response.field1.toString()
        detailName.isEnabled = false
        detailAge.isEnabled = false
        detailName.setText(name)
        detailAge.setText(age)
    }

    private fun setUpCheckBox(check: Boolean) {
        detailCheckbox.also {
            if (check == true) {
                it.isChecked = true
                booleanForCheckUpdate = true
            } else {
                it.isChecked = false
                booleanForCheckUpdate = false
            }

            it.setOnClickListener {_->
                if (it.isChecked) {
                    booleanForCheckUpdate = true
                    println("if" + booleanForCheckUpdate)
                } else {
                    booleanForCheckUpdate = false
                    println("else" + booleanForCheckUpdate)
                }
            }
        }
    }

    private fun setUpRadioButton(list: ArrayList<String>) {
        radioButtonDetail1.also {
            if (list.contains(it.text)) {
                it.isChecked = true
                radioButtonUpdateList.add(it.text.toString())
            } else {
                it.isChecked = false
            }
            var isChecked = it.isChecked
            it.setOnClickListener { _->
                it.isChecked = !isChecked
                isChecked = !isChecked
            }
        }
        radioButtonDetail2.also {
            if (list.contains(it.text)) {
                it.isChecked = true
                radioButtonUpdateList.add(it.text.toString())
            } else {
                it.isChecked = false
            }
            var isChecked = it.isChecked
            it.setOnClickListener { _->
                it.isChecked = !isChecked
                isChecked = !isChecked
            }
        }
        radioButtonDetail3.also {
            if (list.contains(it.text)) {
                it.isChecked = true
                radioButtonUpdateList.add(it.text.toString())
            } else {
                it.isChecked = false
            }
            var isChecked = it.isChecked
            it.setOnClickListener { _->
                it.isChecked = !isChecked
                isChecked = !isChecked
            }
        }
        radioButtonDetail4.also {
            if (list.contains(it.text)) {
                it.isChecked = true
                radioButtonUpdateList.add(it.text.toString())
            } else {
                it.isChecked = false
            }
            var isChecked = it.isChecked
            it.setOnClickListener { _->
                it.isChecked = !isChecked
                isChecked = !isChecked
            }
        }
    }

    private fun deleteItemFromServer(id: String) {
        compositeDisposable = CompositeDisposable()
        val retrofit = RetrofitService2(BASE_URL).retrofit.create(ApiForProject::class.java)
        compositeDisposable.add(retrofit.deleteItem(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                // Delete successful
                println("deteled item")
            },
                { throwable ->
                    // Handle error
                    println(throwable)
                }
            ))
    }

    fun setUpdateImage(view: View) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            //sdk level 33
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //if  permission don't granted make snackbar
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        android.Manifest.permission.READ_MEDIA_IMAGES
                    )
                ) {
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Give Permission", View.OnClickListener {
                            //request permission
                            permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                        }).show()
                    //request permission
                } else {
                    permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                }
                //permission granted
            } else {
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
            //////////////////////////////////////////////////////////////////
        } else {
            //sdk level 33>
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //if  permission don't granted make snackbar
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Give Permission", View.OnClickListener {
                            //request permission
                            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        }).show()
                    //request permission
                } else {
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
                //permission granted
            } else {
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
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
                                this@TestDetailActivity.contentResolver,
                                selectedPicture!!
                            )
                            selectedBitmap = ImageDecoder.decodeBitmap(source)
                            val newBitmap = BitmapFactory.decodeStream(
                                getContentResolver().openInputStream(selectedPicture!!)
                            )
                            detailImage.setImageBitmap(newBitmap)
                        } else {
                            println("--- P12 ---")

                            selectedBitmap = MediaStore.Images.Media.getBitmap(
                                this@TestDetailActivity.contentResolver,
                                selectedPicture
                            )
                            detailImage.setImageBitmap(selectedBitmap)
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
                Toast.makeText(this@TestDetailActivity, "Permission needed!", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun updateItemFromServer(id: String) {
        val image = intent.getStringExtra("image")
        val age = detailAge.text.toString().toInt()
        val name = detailName.text.toString()
        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"
        val reference = storage.reference
        if (selectedPicture == null) {
                radioButtonUpdateList.clear()
                if (radioButtonDetail1.isChecked) {
                    radioButtonUpdateList.add(radioButtonDetail1.text.toString())
                }

                if (radioButtonDetail2.isChecked) {
                    radioButtonUpdateList.add(radioButtonDetail2.text.toString())
                }

                if (radioButtonDetail3.isChecked) {
                    radioButtonUpdateList.add(radioButtonDetail3.text.toString())
                }

                if (radioButtonDetail4.isChecked) {
                    radioButtonUpdateList.add(radioButtonDetail4.text.toString())
                }

            compositeDisposable = CompositeDisposable()
            val retrofit = RetrofitService2(BASE_URL).retrofit.create(
                ApiForProject::class.java
            )

            println("checkbox" + booleanForCheckUpdate)
            println("radiobutton" + radioButtonUpdateList)

            val request = RequestForApi(
                age,
                name,
                booleanForCheckUpdate,
                radioButtonUpdateList,
                image!!
            )

            compositeDisposable.add(
                retrofit.updateItem(id, request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        println("success")
                    }, {
                        println(it)
                    })
            )
            finish()
            Toast.makeText(this, "Successfully updated", Toast.LENGTH_SHORT).show()
        } else {
            radioButtonUpdateList.clear()
                if (radioButtonDetail1.isChecked) {
                    radioButtonUpdateList.add(radioButtonDetail1.text.toString())
                }

                if (radioButtonDetail2.isChecked) {
                    radioButtonUpdateList.add(radioButtonDetail2.text.toString())
                }

                if (radioButtonDetail3.isChecked) {
                    radioButtonUpdateList.add(radioButtonDetail3.text.toString())
                }

                if (radioButtonDetail4.isChecked) {
                    radioButtonUpdateList.add(radioButtonDetail4.text.toString())
                }

            compositeDisposable = CompositeDisposable()
            val retrofit = RetrofitService2(BASE_URL).retrofit.create(
                ApiForProject::class.java
            )
            val imagesReference = reference.child("karimimages").child(imageName)
            imagesReference.putFile(selectedPicture!!).addOnSuccessListener { taskSnapshot ->
                val uploadedPictureReference =
                    storage.reference.child("karimimages").child(imageName)
                uploadedPictureReference.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    val imagePet = downloadUrl

                    println("checkbox" + booleanForCheckUpdate)
                    println("radiobutton" + radioButtonUpdateList)

                    val request = RequestForApi(
                        age,
                        name,
                        booleanForCheckUpdate,
                        radioButtonUpdateList,
                        imagePet
                    )

                    compositeDisposable.add(
                        retrofit.updateItem(id, request)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                println("success")
                            }, {
                                println(it)
                            })
                    )
                    finish()
                    Toast.makeText(this, "Successfully updated", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}



