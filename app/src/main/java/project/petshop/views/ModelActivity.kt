package project.petshop.views

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_model.*
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import project.petshop.R
import project.petshop.ml.ModelUnquant
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import android.util.Log.d as logD

var PERMISSON_CODE = 100
val REQUEST_IMAGE_CAPTURE = 1
val REQUEST_GALLERY = 2

private var bitmap: Bitmap?=null
private var labels: List<String>? = null



class ModelActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_model)
        //press gallery
        gallery.setOnClickListener {
            detect.text =""
            requestPermisson()
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                startActivityForResult(it, REQUEST_GALLERY)
            }
        }
        //Press Recommend bnt
        recommend.setOnClickListener {
            try {
                val model = ModelUnquant.newInstance(this)
//                 INPUT and OUTPUT
//                [  1 224 224   3] <class 'numpy.float32'>
//                [1 4] <class 'numpy.float32'>
                val bitmap1 = Bitmap.createScaledBitmap(bitmap!!, 224, 224, true)
                val input = ByteBuffer.allocateDirect(224*224*3*4).order(ByteOrder.nativeOrder())
                for (y in 0 until 224) {
                for (x in 0 until 224) {
                    val px = bitmap1.getPixel(x, y)

                    // Get channel values from the pixel value.
                    val r = Color.red(px)
                    val g = Color.green(px)
                    val b = Color.blue(px)
                    // changto nomalize
                    val rf = (r - 127) / 255f
                    val gf = (g - 127) / 255f
                    val bf = (b - 127) / 255f

                    input.putFloat(rf)
                    input.putFloat(gf)
                    input.putFloat(bf)
                }
            }
                var inputFeature0 =
                    TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
                inputFeature0.loadBuffer(input)
                // dectect time // Run model
                val outputs = model.process(inputFeature0)
                val outputFeature0 = outputs.outputFeature0AsTensorBuffer
                val probabilities = outputFeature0.floatArray.toMutableList()
                try {
                    val reader = BufferedReader(
                        InputStreamReader(assets.open("labels.txt"))
                    )
                    val max = probabilities.maxOf {it}
                    for (i in 0..20) {
                        val label: String = reader.readLine()!!
                        val probability = probabilities.get(i)!!
                        Log.i("TAG", "onCreate: $probability to be $label")
                        if (probability==max){
                            detect.text = label

                            //Press See recommend btn
                            see_recommend.setOnClickListener {
                                Intent(this, SeeMoreActivity::class.java).apply {
                                    putExtras(Bundle().also {
                                        if (label.lowercase().contains("dog")) {
                                            it.putString("tag", "dog")
                                        } else {
                                            it.putString("tag", "cat")
                                        }
                                    })
                                    startActivity(this)
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
//                    Toast.makeText(this, "1+$e",Toast.LENGTH_LONG).show()
                }
                model.close()
            }catch (e: Exception){
                Toast.makeText(this, "2+$e",Toast.LENGTH_LONG).show()
            }
        }
    }

    // call camera return a bit map
    private fun TakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: Exception) {
            Toast.makeText(this, "$e",Toast.LENGTH_LONG).show()
        }
    }
    // request permission write to gallery
    private fun hasWriteExternalStoragePermission() =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    private fun hasReadExternalStoragePermission() =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    // request to use camera
    private fun hasCameraPermission() =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    // check request
    private fun requestPermisson() {
        val permissionToRequest = mutableListOf<String>()
        if (!hasWriteExternalStoragePermission()) {
            permissionToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!hasReadExternalStoragePermission()) {
            permissionToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (!hasCameraPermission()) {
            permissionToRequest.add(Manifest.permission.CAMERA)
        }
        if (permissionToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionToRequest.toTypedArray(),
                PERMISSON_CODE
            )
        }
    }
    // override send request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSON_CODE && grantResults.isNotEmpty()) {
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    logD("permisson request", "${permissions[i]} granted")
                }
            }

        }
    }
    // override take result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_GALLERY) {
            val uri = data?.data
            imageView.setImageURI(uri)
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
        }
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(imageBitmap)
            bitmap = imageBitmap

        }
    }

}