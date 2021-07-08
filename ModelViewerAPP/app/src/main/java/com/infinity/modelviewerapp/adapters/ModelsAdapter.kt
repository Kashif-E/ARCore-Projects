package com.infinity.modelviewerapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.infinity.modelviewerapp.R
import com.infinity.modelviewerapp.databinding.ModelsCardBinding
import com.infinity.modelviewerapp.models.Models

class ModelsAdapter : RecyclerView.Adapter<ModelsAdapter.ModelsViewHolder>() {

    inner class ModelsViewHolder(private val itemViewBinding : ModelsCardBinding) : RecyclerView.ViewHolder(itemViewBinding.root)

    {

        fun bindView(modelItem : Models) {
            itemViewBinding.apply {
                model = modelItem

            }

             itemViewBinding.root.setOnClickListener {
                onItemClickListener?.let {
                    it(modelItem)

                }
            }
        }
    }

    private val differCallBack  = object : DiffUtil.ItemCallback<Models>()
    {

        override fun areItemsTheSame(oldItem: Models, newItem: Models): Boolean {
            return  oldItem.Model == newItem.Model
        }

        override fun areContentsTheSame(oldItem: Models, newItem: Models): Boolean {
            return  oldItem==newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallBack)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelsViewHolder{
        return ModelsViewHolder(

            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.models_card, parent, false
            )
        )
    }

    private var onItemClickListener: ((Models) -> Unit)? = null
    override fun onBindViewHolder(holder: ModelsViewHolder, position: Int) {

        val movieItem= differ.currentList[position]
        holder.bindView(movieItem)
    }




    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    fun setOnItemClickListener(listener: (Models) -> Unit) {
        onItemClickListener = listener

    }
}
