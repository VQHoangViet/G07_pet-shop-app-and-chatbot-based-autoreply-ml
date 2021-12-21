package project.petshop.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_see_more.*
import kotlinx.android.synthetic.main.activity_see_more.toolbar
import project.petshop.R
import project.petshop.adapters.ProductAdapter
import project.petshop.objects.Product

class SeeMoreActivity : AppCompatActivity() {
    val products = ArrayList<Product>()
    var adapter: ProductAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_more)

        // Back button on toolbar
        toolbar.setNavigationOnClickListener { onBackPressed() }

        adapter = ProductAdapter(this, R.layout.viewholder_popular, products)
        recyclerView.adapter = adapter

        // Get all products if no type specified
        val extras = intent.extras
        if (extras == null) {
            Product.get()
                .addOnSuccessListener { addData(it) }
        } else if (extras.containsKey("type")) {
            val type = extras.getString("type")!!
            toolbar.title = type
            Product.getByType(type)
                .addOnSuccessListener { addData(it) }
        }
    }

    private fun addData(querySnapshot: QuerySnapshot) {
        val documents = querySnapshot.documents
        for (doc in documents) {
            val product = Product(doc)
            products.add(product)
            adapter!!.notifyItemInserted(products.size - 1)
        }
    }
}