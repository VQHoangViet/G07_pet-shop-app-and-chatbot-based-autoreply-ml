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
import androidx.recyclerview.widget.RecyclerView
import project.petshop.R
import project.petshop.objects.Product
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

        if (layoutId == R.layout.viewholder_cat) {
            product.setPic(context, categoryPic)
            categoryName.text = product.name
        } else if (layoutId == R.layout.viewholder_popular) {
            val categoryPrice = holder.categoryPrice
            val addBtn = holder.addBtn

            product.setPic(context, categoryPic)
            categoryName.text = product.name
            categoryPrice!!.text = product.price.toString()

            addBtn!!.setOnClickListener {
                // TODO(Add product to cart)
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

    override fun getItemCount(): Int {
        return products.size
    }

    class ViewHolder(layoutId: Int, itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryPic : ImageView
        val categoryName : TextView
        val categoryPrice : TextView?
        val addBtn : Button?

        init {
            // Define ViewHolder's View
            categoryPic = itemView.findViewById(R.id.categoryPic)
            categoryName = itemView.findViewById(R.id.categoryName)
            if (layoutId == R.layout.viewholder_popular) {
                categoryPrice = itemView.findViewById(R.id.categoryPrice)
                addBtn = itemView.findViewById(R.id.addBtn)
            } else {
                categoryPrice = null
                addBtn = null
            }
        }
    }
}