package project.petshop.views

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.synthetic.main.activity_home.*
import project.petshop.R
import project.petshop.adapters.ProductAdapter
import project.petshop.extensions.Extensions.toast
import project.petshop.objects.Product
import project.petshop.utils.FirebaseUtils.firebaseAuth

class HomeActivity : AppCompatActivity() {
    val products = ArrayList<Product>()
    val popular = ArrayList<Product>()
    var adapterQuickView: ProductAdapter? = null
    var adapterPopular: ProductAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
// sign out a user

//        btnSignOut.setOnClickListener {
//            firebaseAuth.signOut()
//            startActivity(Intent(this, SignInActivity::class.java))
//            toast("signed out")
//            finish()
//        }
        /////// Dat su dụng bức ảnh này thay thế nút đề xuất khi nào có mọi người chỉnh lại nha
        imageView3.setOnClickListener{
            Intent(this,ModelActivity::class.java).also{
                startActivity(it)
            }
        }/////////
        adapterQuickView = ProductAdapter(this, R.layout.viewholder_cat, products)
        adapterPopular = ProductAdapter(this, R.layout.viewholder_popular, popular)
        recyclerViewQuickView.adapter = adapterQuickView
        recyclerViewPopular.adapter = adapterPopular

        Product.getRecent()
            .addOnSuccessListener { querySnapshot ->
                val documents = querySnapshot.documents
                for (doc in documents) {
                    val product = Product(doc)
                    products.add(product)
                    adapterQuickView!!.notifyItemInserted(products.size - 1)
                }
            }

        Product.get()
            .addOnSuccessListener { querySnapshot ->
                val documents = querySnapshot.documents
                for (doc in documents) {
                    val product = Product(doc)
                    popular.add(product)
                    adapterPopular!!.notifyItemInserted(popular.size - 1)
                }
            }
    }
}