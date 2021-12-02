package project.petshop.views

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.activity_home.*
import project.petshop.R
import project.petshop.adapters.ProductAdapter
import project.petshop.objects.Product

class CartActivity : AppCompatActivity() {
    private lateinit var sharedPref: SharedPreferences
    private var cart = HashSet<String>()
    private val products = ArrayList<Product>()

    var shipping = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // Back button on toolbar
        toolbar.setNavigationOnClickListener { onBackPressed() }

        // Init RecyclerView
        val adapter = ProductAdapter(this, R.layout.cart_view, products)
        recyclerViewCart.adapter = adapter

        // Get data from SharedPreferences
        sharedPref = getSharedPreferences("CART", Context.MODE_PRIVATE)
        if (sharedPref.contains("cart")) {
            cart = sharedPref.getStringSet("cart", null) as HashSet<String>
        } else {
            // TODO: Cart is empty
            return
        }

        // Get data from Firestore
        for (id : String in cart) {
            Product.get(id)
                .addOnSuccessListener {doc ->
                    products.add(Product(doc))
                    adapter.notifyItemInserted(products.size - 1)
                }
        }

        textView9.setOnClickListener { view ->
            view.isEnabled = false
            view.alpha = 0.5f
            view.backgroundTintList = resources.getColorStateList(R.color.gray, null)
            (view as TextView).text = "Pending…"
            cover.visibility = View.VISIBLE
        }
    }

    fun updateTotal() {
        var total : Long = 0
        for (product : Product in products) {
            val id = product.id
            total += product.price!! * sharedPref.getInt("$id@cart", 0)
        }
        itemTotal.text = "$$total"
        delTxt.text = "$$shipping"
        totalTxt.text = "$" + (total + shipping)
    }
}