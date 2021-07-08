package com.infinity.modelviewerapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.infinity.modelviewerapp.models.Category
import com.infinity.modelviewerapp.models.Models
import com.infinity.modelviewerapp.repository.repository

class SharedViewModel(val repo : repository) : ViewModel(){
    var category : MutableLiveData<MutableList<Category>> = MutableLiveData()
    var furniture : MutableLiveData<MutableList<Models>> = MutableLiveData()
    var utensils : MutableLiveData<MutableList<Models>> = MutableLiveData()
    var electronics : MutableLiveData<MutableList<Models>> = MutableLiveData()
    var clothes : MutableLiveData<MutableList<Models>> = MutableLiveData()

    var modelUrl  = MutableLiveData<String>()
    init {
        getCategories()
        getClothes()
        getFurniture()
        getUtensils()
        getElectronics()
    }
    fun setModelUrl(url : String){
        modelUrl.value = url
    }
    private fun getCategories()
    {
         category =repo.getCategories()
    }
    fun getFurniture()
    {
      furniture = repo.getModels("Furniture")
    }
    fun getUtensils()
    {
        utensils = repo.getModels("Utensils")
    }
    fun getClothes()
    {
        clothes = repo.getModels("Clothes")
    }
    fun getElectronics()
    {
        electronics = repo.getModels("Electronics")
    }
}