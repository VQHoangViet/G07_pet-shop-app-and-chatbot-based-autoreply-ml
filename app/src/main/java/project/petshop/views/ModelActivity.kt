package project.petshop.views

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_model.*
import project.petshop.R

var PERMISSON_CODE = 0

class ModelActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_model)
        upload.setOnClickListener(){
            requestPermisson()
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
}