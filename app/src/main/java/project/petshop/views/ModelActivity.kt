package project.petshop.views

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import kotlinx.android.synthetic.main.activity_model.*
import project.petshop.R

var PERMISSON_CODE = 0
val REQUEST_IMAGE_CAPTURE = 1


class ModelActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_model)
        upload.setOnClickListener {
            requestPermisson()
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also{
                it.type = "image/*"
                startActivityForResult(it, 0)


                }
            }
        }

    private fun TakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {

        }
    }
    private fun hasWriteExternalStoragePermission() =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    private fun hasCameraPermission() =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    private fun requestPermisson(){
        val permissionToRequest = mutableListOf<String>()
        if(!hasWriteExternalStoragePermission()){
            permissionToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if(!hasCameraPermission()){
            permissionToRequest.add(Manifest.permission.CAMERA)
        }
        if(permissionToRequest.isNotEmpty()){
            ActivityCompat.requestPermissions(this,permissionToRequest.toTypedArray(),PERMISSON_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSON_CODE && grantResults.isNotEmpty()){
            for (i in  grantResults.indices){
                if(grantResults[i]==PackageManager.PERMISSION_GRANTED){
                    Log.d("permisson request","${permissions[i]} granted")
                }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == 0){
            var uri = data?.data
            imageView.setImageURI(uri)
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            val imageBitmap = data.extras.get("data") as Bitmap
//            imageView.setImageBitmap(imageBitmap)
        }
    }

}