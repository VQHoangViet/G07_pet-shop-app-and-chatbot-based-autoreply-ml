package project.petshop.views

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.cart_btn
import kotlinx.android.synthetic.main.activity_home.profile
import project.petshop.R
import project.petshop.adapters.ProductAdapter
import project.petshop.objects.Product
import project.petshop.objects.User
import kotlin.collections.ArrayList

class HomeActivity : AppCompatActivity() {
    val products = ArrayList<Product>()
    val popular = ArrayList<Product>()
    var adapterQuickView: ProductAdapter? = null
    var adapterPopular: ProductAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        checkUser()

        /////// Dat su dụng bức ảnh này thay thế nút đề xuất khi nào có mọi người chỉnh lại nha
        constraintLayout.setOnClickListener{
            Intent(this,ModelActivity::class.java).also{
                startActivity(it)
            }
        }/////////

        cart_btn.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        profile.setOnClickListener {
            val intent = Intent(this@HomeActivity, ProfileActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

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
                    adapterQuickView!!.notifyDataSetChanged()
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

    private fun checkUser() {
        // Check if user is logged in, if not then open Sign in
        if (Firebase.auth.currentUser == null) {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            return
        }

        // Check if user has data,
        // if yes, update view with data
        // if not then open Edit Profile (no email edit)
        User.get(Firebase.auth.currentUser!!.uid)
            .addOnSuccessListener { docUser ->
                if (!docUser.exists()) {
                    val intent = Intent(this, ProfileEditActivity::class.java)
                    val bundle = Bundle()
                    bundle.putBoolean("emailEdit", false)
                    intent.putExtras(bundle)
                    startActivity(intent)
                    return@addOnSuccessListener
                }

                val user = User(docUser)
                textView.text = getString(R.string.hi, user.name)
                user.setAvatar(this, avatar)
            }
    }

    override fun onResume() {
        super.onResume()
        checkUser()
    }

    override fun onRestart() {
        super.onRestart()
        checkUser()
    }
}