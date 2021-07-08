package com.infinity.modelviewerapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.infinity.modelviewerapp.R
import com.infinity.modelviewerapp.databinding.ReccyclerViewCardBinding
import com.infinity.modelviewerapp.models.Category


class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(val itemViewBinding : ReccyclerViewCardBinding) : RecyclerView.ViewHolder(itemViewBinding.root)

    {

        fun bindView(category : Category) {
            itemViewBinding.apply {
               categoryItem = category
            }

            itemViewBinding.root.setOnClickListener {
                onItemClickListener?.let {
                    it(category)

                }
            }
        }
    }

    private val differCallBack  = object : DiffUtil.ItemCallback<Category>()
    {

        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return  oldItem.CategoryName == newItem.CategoryName
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return  oldItem==newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallBack)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder{
        return CategoryViewHolder(

            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.reccycler_view_card, parent, false
            )
        )
    }

    private var onItemClickListener: ((Category) -> Unit)? = null
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {

        val movieItem= differ.currentList[position]
        holder.bindView(movieItem)
    }




    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    fun setOnItemClickListener(listener: (Category) -> Unit) {
        onItemClickListener = listener

    }
}
