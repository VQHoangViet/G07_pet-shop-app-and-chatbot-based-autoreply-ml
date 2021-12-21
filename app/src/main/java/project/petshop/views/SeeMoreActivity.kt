package project.petshop.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_see_more.*
import kotlinx.android.synthetic.main.activity_see_more.toolbar
import project.petshop.R
import project.petshop.adapters.ProductAdapter
import project.petshop.objects.Product

class SeeMoreActivity : AppCompatActivity() {
    val popular = ArrayList<Product>()
    var adapter: ProductAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_more)

        // Back button on toolbar
        toolbar.setNavigationOnClickListener { onBackPressed() }

        adapter = ProductAdapter(this, R.layout.viewholder_popular, popular)
        recyclerView.adapter = adapter

        Product.get()
            .addOnSuccessListener { querySnapshot ->
                val documents = querySnapshot.documents
                for (doc in documents) {
                    val product = Product(doc)
                    popular.add(product)
                    adapter!!.notifyItemInserted(popular.size - 1)
                }
            }
    }
}