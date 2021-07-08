package com.infinity.augmentedfacessceneform

import android.view.Display
import androidx.lifecycle.ViewModel
import com.google.ar.sceneform.rendering.ModelRenderable

class ViewModel : ViewModel() {
    private val models = mutableListOf(Model(R.drawable.black , "Black"),
        Model (R.drawable.yellow , "yellow"))
   val array : MutableList<ModelRenderable> = mutableListOf()
            fun  setList(mutableList: MutableList<ModelRenderable>) {
                array.addAll(mutableList)

            }
    fun addelements(model : ModelRenderable)
    {
        array.add(model)
    }
    fun  getList() = array
    fun getmodels() = models


}