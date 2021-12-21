package project.petshop.objects

import android.content.Context
import android.widget.ImageView
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.squareup.picasso.Picasso
import project.petshop.R
import project.petshop.utils.FirebaseUtils
import java.time.Instant
import java.util.*

class Product() {
    var id : String? = null
    var name: String? = null
    var des: String? = null
    var type: String? = null
    var tag: String? = null
    var price: Long? = 0
    var pic: String? = null
    var dateAdded : Date? = null

    constructor(doc : DocumentSnapshot) : this() {
        id = doc.id
        name = doc.getString("name")
        des = doc.getString("des")
        type = doc.getString("type")
        tag = doc.getString("tag")
        price = doc.getLong("price")
        pic = doc.getString("pic")
        dateAdded = doc.getDate("dateAdded")
    }

    fun set() : Task<String> {
        val product = hashMapOf(
            "name" to name,
            "des" to des,
            "type" to type,
            "tag" to tag,
            "price" to price,
            "pic" to pic
        )

        // Check if the document exists on Firestore,
        // if yes : call set(),
        // otherwise : call add() to generate a new id by Firestore ( if not set )
        if (id != null) {
            return FirebaseUtils.db.collection(collection).document(id!!).set(product)
                .continueWith {
                    return@continueWith id
                }
        } else {
            // Return id from the new DocumentReference,
            // if null : return empty string
            product["dateAdded"] = Date.from(Instant.now())
            return FirebaseUtils.db.collection(collection).add(product)
                .continueWith {task ->
                    return@continueWith task.result?.path ?: ""
                }
        }
    }

    fun setPic(context : Context, imageView : ImageView) {
        if (pic != null) {
//            val p = Picasso.get();
//            p.isLoggingEnabled = true
//            p.load("https://petprince.vn/multidata/thuc-an-cho-cho-thit-cuu.jpg")
//                .placeholder(R.drawable.ic_error)
//                .into(imageView)

            // Get download url, and let Picasso load the image url into imageView
            FirebaseUtils.storage.getReferenceFromUrl(pic!!).downloadUrl
                .addOnSuccessListener { uri ->
                    Picasso.get().load(uri)
                        .placeholder(R.drawable.ic_error)
                        .into(imageView)
                }
        } else {
            Picasso.get().load(R.drawable.ic_error).into(imageView)
        }
    }

    companion object {
        const val collection = "product"

        fun get() : Task<QuerySnapshot> {
            return FirebaseUtils.db.collection(collection).get()
        }

        fun getRecent() : Task<QuerySnapshot> {
            // Get 8 newest products
            return FirebaseUtils.db.collection(collection)
                .orderBy("dateAdded")
                .limit(8)
                .get()
        }

        fun get(id : String) : Task<DocumentSnapshot> {
            return FirebaseUtils.db.collection(collection).document(id).get()
        }

        fun getByType(type : String) : Task<QuerySnapshot> {
            // FIXME: Type on Firebase must be all lowercase
            return FirebaseUtils.db
                .collection(collection)
                .whereEqualTo("type", type.lowercase()).get()
        }

        fun getByTag(tag : String) : Task<ArrayList<Product>> {
            return get().continueWith {
                val list = ArrayList<Product>()

                if (!it.isSuccessful) {
                    return@continueWith list
                }

                val documents = it.result.documents
                for (doc in documents) {
                    val product = Product(doc)
                    if (product.tag != null) {
                        if (product.tag!!.contains(tag)) {
                            list.add(Product(doc))
                        }
                    }
                }
                return@continueWith list
            }
        }
    }
}