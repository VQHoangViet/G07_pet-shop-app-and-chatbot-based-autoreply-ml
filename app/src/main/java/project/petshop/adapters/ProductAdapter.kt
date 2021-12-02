package project.petshop.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import project.petshop.R
import project.petshop.objects.Product
import project.petshop.views.CartActivity
import project.petshop.views.ProductDetailsActivity

class ProductAdapter(val context: Context, val layoutId: Int, val products: ArrayList<Product>) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        var view : View = LayoutInflater.from(parent.context)
            .inflate(layoutId, parent, false)
        return ViewHolder(layoutId, view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Apply data into each view
        val product = products[position]
        val categoryPic = holder.categoryPic
        val categoryName = holder.categoryName

        product.setPic(context, categoryPic)
        categoryName.text = product.name

        if (layoutId == R.layout.viewholder_popular) {
            val categoryPrice = holder.categoryPrice
            val addBtn = holder.addBtn

            product.setPic(context, categoryPic)
            categoryPrice!!.text = product.price.toString()

            addBtn!!.setOnClickListener {
                // TODO(Add product to cart)
            }
        } else if (layoutId == R.layout.cart_view) {
            val sharedPref = (context as AppCompatActivity).getSharedPreferences("CART", Context.MODE_PRIVATE)
            val categoryPrice = holder.categoryPrice
            val categoryTotal = holder.categoryTotal
            val categoryCount = holder.categoryCount
            val minusCardBtn = holder.minusCardBtn
            val plusCardBtn = holder.plusCardBtn

            val id = product.id
            var amount = sharedPref.getInt("$id@cart", 0)

            categoryPrice!!.text = product.price.toString()
            categoryCount!!.text = amount.toString()
            categoryTotal!!.text = (product.price!! * amount).toString()
            (context as CartActivity).updateTotal()

            minusCardBtn!!.setOnClickListener {
                if (categoryCount.text.isEmpty()) {
                    amount = 1
                    categoryCount.text = "1"
                } else if (amount > 1) {
                    amount--
                    categoryCount.text = amount.toString()
                }
                categoryTotal!!.text = (product.price!! * amount).toString()
                sharedPref.edit().putInt("$id@cart", amount).apply()
                context.updateTotal()
            }
            plusCardBtn!!.setOnClickListener {
                if (categoryCount.text.isEmpty()) {
                    amount = 1
                    categoryCount.text = "1"
                } else if (amount < 999) {
                    amount++
                    categoryCount.text = amount.toString()
                }
                categoryTotal!!.text = (product.price!! * amount).toString()
                sharedPref.edit().putInt("$id@cart", amount).apply()
                context.updateTotal()
            }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProductDetailsActivity::class.java)
            val bundle = Bundle()
            bundle.putString("product", product.id)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    // Get total of cart
    fun getTotal() {

    }

    override fun getItemCount(): Int {
        return products.size
    }

    class ViewHolder(layoutId: Int, itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryPic : ImageView
        val categoryName : TextView
        var categoryPrice : TextView? = null
        var categoryTotal : TextView? = null
        var categoryCount : TextView? = null
        var addBtn : Button? = null
        var minusCardBtn : ImageView? = null
        var plusCardBtn : ImageView? = null

        init {
            // Define ViewHolder's View
            categoryPic = itemView.findViewById(R.id.categoryPic)
            categoryName = itemView.findViewById(R.id.categoryName)
            if (layoutId == R.layout.viewholder_popular) {
                categoryPrice = itemView.findViewById(R.id.categoryPrice)
                addBtn = itemView.findViewById(R.id.addBtn)
            } else if (layoutId == R.layout.cart_view) {
                categoryPrice = itemView.findViewById(R.id.categoryPrice)
                categoryTotal = itemView.findViewById(R.id.categoryTotal)
                categoryCount = itemView.findViewById(R.id.categoryCount)
                minusCardBtn = itemView.findViewById(R.id.minusCardBtn)
                plusCardBtn = itemView.findViewById(R.id.plusCardBtn)
            }
        }
    }
}