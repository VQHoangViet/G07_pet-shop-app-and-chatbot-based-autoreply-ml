package project.petshop.views

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import kotlinx.android.synthetic.main.product_information.*
import project.petshop.R
import project.petshop.extensions.Extensions.toast
import project.petshop.objects.Product

class ProductDetailsActivity : AppCompatActivity() {
    var product : Product? = null
    var amount : Int = 1

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
                descriptionTxt.text = product!!.des

                minusBtn.setOnClickListener {
                    if (numberOrderTxt.text.isEmpty()) {
                        numberOrderTxt.setText("1")
                        amount = 1
                        return@setOnClickListener;
                    }
                    if (amount > 1) {
                        amount--;
                        numberOrderTxt.setText(amount.toString())
                    }
                }
                plusBtn.setOnClickListener {
                    if (numberOrderTxt.text.isEmpty()) {
                        numberOrderTxt.setText("1")
                        amount = 1
                        return@setOnClickListener;
                    }
                    if (amount < 999) {
                        amount++
                        numberOrderTxt.setText(amount.toString())
                    }
                }
                numberOrderTxt.addTextChangedListener {
                    var a : Int
                    try {
                        a = numberOrderTxt.text.toString().toInt()
                    } catch (e : Exception) {
                        return@addTextChangedListener
                    }
                    if (a < 1) {
                        a = 1
                        toast("Amount cannot be smaller than 1.")
                        numberOrderTxt.setText("1")
                    }
                    amount = a
                }

                textView15.setOnClickListener {
                    val id = product!!.id!!
                    val sharedPref = getSharedPreferences("CART", Context.MODE_PRIVATE)
                    var cart : Set<String>? = null

                    // Get data from SharedPreferences
                    if (sharedPref.contains("cart")) {
                        cart = sharedPref.getStringSet("cart", null)
                    }
                    if (cart == null) {
                        cart = HashSet()
                    }

                    // Add/update new item in Set
                    if (!cart.contains(id)) {
                        cart = cart + id
                    } else {
                        amount += sharedPref.getInt("$id@cart", 0)
                    }

                    // Write Set into SharedPreferences
                    with (sharedPref.edit()) {
                        putStringSet("cart", cart)
                        putInt("$id@cart", amount)
                        apply()
                    }

                    // Exit activity
                    finish()
                }
            }
    }
}