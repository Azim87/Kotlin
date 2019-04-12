package com.kubatov.myapplication

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_dialog.view.*
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    var currentPath: String? = null
    val TAKE_PICTURE = 1
    val SELECT_PICTURE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showDialog()
        // initView()
    }

    /*fun initView(){
        mRecyclerView.layoutManager = GridLayoutManager(this, 3)
        mRecyclerView.adapter = TestAdapter()
    }*/

    private fun showDialog() {
        float_button.setOnClickListener() {
            val mDialogView = LayoutInflater
                .from(this)
                .inflate(R.layout.custom_dialog, null)


            val customBuilder = android.support.v7.app.AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("выбрать ")

            val mAlertDialog = customBuilder.show()
            mDialogView.camera_tv.setOnClickListener() {
                mAlertDialog.dismiss()
                openCamera()

            }
            mDialogView.gallery_tv.setOnClickListener() {
                openGallery()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == TAKE_PICTURE && resultCode == Activity.RESULT_OK) {
            try {
                val file = File(currentPath)
                val uri = Uri.fromFile(file)
                image_view.setImageURI(uri)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        if (requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK ){
            try {
                val uri = data!!.data
                val inputStr : InputStream?
                inputStr = contentResolver.openInputStream(uri)

                val image : Bitmap?
                image = BitmapFactory.decodeStream(inputStr)
                image_view.setImageBitmap(image)

            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    fun openGallery(){
        val intent = Intent()
        intent.type ="image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_PICTURE )
    }

    fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImage()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (photoFile != null) {
                val photoUri = FileProvider.getUriForFile(this, "com.kubatov.myapplication.fileProvider", photoFile)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(intent, TAKE_PICTURE)
            }
        }
    }
    fun createImage(): File {
        val timeStamp = SimpleDateFormat("dd,MM,yyyy").format(Date())
        val imageNAme = "JPEG_" + timeStamp + "_"
        var storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        var image = File.createTempFile(imageNAme, ".jpg", storageDir)
        currentPath = image.absolutePath
        return image
    }
}


