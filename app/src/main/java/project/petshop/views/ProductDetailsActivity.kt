package project.petshop.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.product_information.*
import project.petshop.R
import project.petshop.adapters.ProductAdapter
import project.petshop.objects.Product

class ProductDetailsActivity : AppCompatActivity() {
    var product : Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_information)

        val bundle = intent.extras
        val id = bundle!!.getString("product")
        Product.get(id!!)
            .addOnSuccessListener { documentSnapshot ->
                product = Product(documentSnapshot)
                titleTxt.text = product!!.name
                priceTxt.text = product!!.price.toString()
                product!!.setPic(this, foodPic)
                descriptionTxt.text = product!!.description
            }
    }
}