package project.petshop.utils

import android.annotation.SuppressLint
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

@SuppressLint("StaticFieldLeak")
object FirebaseUtils {
    val db = Firebase.firestore
    val storage = Firebase.storage
}