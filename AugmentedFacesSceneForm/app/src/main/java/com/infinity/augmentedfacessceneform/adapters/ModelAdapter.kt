package com.infinity.augmentedfacessceneform.adapters


import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.infinity.augmentedfacessceneform.Model
import com.infinity.augmentedfacessceneform.databinding.ItemModelBinding


const val SELECTED_MODEL_COLOR = Color.YELLOW
const val UNSELECTED_MODEL_COLOR = Color.LTGRAY

class ModelAdapter(
    val models: List<Model>
) : RecyclerView.Adapter<ModelAdapter.ModelViewHolder>() {

    var selectedModel = MutableLiveData<Model>()
    private var selectedModelIndex = 0

    inner class ModelViewHolder(private val itemViewBinding: ItemModelBinding) :
        RecyclerView.ViewHolder(itemViewBinding.root) {
        fun bind(model: Model) {

            with(itemViewBinding) {
                ivThumbnail.setImageResource(model.imageResourceId)
                tvTitle.text = model.title
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
        val itemBinding =
            ItemModelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ModelViewHolder(itemBinding)
    }

    override fun getItemCount() = models.size

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        if (selectedModelIndex == holder.layoutPosition) {
            holder.itemView.setBackgroundColor(SELECTED_MODEL_COLOR)
            selectedModel.value = models[holder.layoutPosition]
        } else {
            holder.itemView.setBackgroundColor(UNSELECTED_MODEL_COLOR)
        }
        holder.itemView.apply {
            setOnClickListener {
                selectModel(holder)
            }
        }
        holder.bind(models[position])
    }

    private fun selectModel(holder: ModelViewHolder) {
        if (selectedModelIndex != holder.layoutPosition) {
            holder.itemView.setBackgroundColor(SELECTED_MODEL_COLOR)
            notifyItemChanged(selectedModelIndex)
            selectedModelIndex = holder.adapterPosition
            selectedModel.value = models[holder.layoutPosition]
        }
    }
}