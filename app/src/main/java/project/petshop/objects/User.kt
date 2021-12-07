package project.petshop.objects

import android.content.Context
import android.util.Log
import android.widget.ImageView
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.squareup.picasso.Picasso
import project.petshop.R
import project.petshop.utils.FirebaseUtils
import java.util.*

class User() {
    var uid : String? = null
    var name: String? = null
    var dob: Date? = null
    var phone: String? = null
    var email: String? = null
    var gender: Long? = 0
    var address: String? = null

    constructor(doc : DocumentSnapshot) : this() {
        uid = doc.id
        name = doc.getString("name")
        dob = doc.getDate("dob")
        phone = doc.getString("phone")
        email = doc.getString("email")
        gender = doc.getLong("gender")
        address = doc.getString("address")
    }

    fun set() : Task<String>? {
        val user = hashMapOf(
            "name" to name,
            "dob" to dob,
            "phone" to phone,
            "email" to email,
            "gender" to gender,
            "address" to address
        )

        // Check if the document exists on Firestore,
        // if yes : call set(),
        // otherwise : call add() to generate a new id by Firestore ( if not set )
        if (uid != null) {
            return FirebaseUtils.db.collection(collection).document(uid!!).set(user)
                .continueWith {
                    return@continueWith uid
                }
        } else {
            Log.e("USER", "set: uid is null")
            return null
        }
    }

    fun setAvatar(context : Context, imageView : ImageView) {
        // FIXME: Need to upload *.jpg as avatars only
        val avatar = "gs://petshop-auth.appspot.com/avatars/%s.jpg".format(uid)
        // Get download url, and let Picasso load the image url into imageView
        FirebaseUtils.storage.getReferenceFromUrl(avatar).downloadUrl
            .addOnSuccessListener { uri ->
                Picasso.get().load(uri)
                    .placeholder(R.drawable.default_avatar)
                    .into(imageView)
            }.addOnFailureListener {
                Picasso.get().load(R.drawable.default_avatar).into(imageView)
            }
    }

    companion object {
        const val collection = "user"

        fun get() : Task<QuerySnapshot> {
            return FirebaseUtils.db.collection(collection).get()
        }

        fun get(id : String) : Task<DocumentSnapshot> {
            return FirebaseUtils.db.collection(collection).document(id).get()
        }
    }
}