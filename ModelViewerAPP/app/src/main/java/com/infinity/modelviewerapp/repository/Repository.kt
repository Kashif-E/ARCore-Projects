package com.infinity.modelviewerapp.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.infinity.modelviewerapp.models.Category
import com.infinity.modelviewerapp.models.Models

class repository {

    private val categoryRef = Firebase.firestore.collection("categories")
    val categoriesList = MutableLiveData<MutableList<Category>>()
    val furnitureList = MutableLiveData<MutableList<Models>>()
    val utensilList = MutableLiveData<MutableList<Models>>()
    val clothesList = MutableLiveData<MutableList<Models>>()
    val electronicsList = MutableLiveData<MutableList<Models>>()

    fun getCategories(): MutableLiveData<MutableList<Category>> {
        val listData = mutableListOf<Category>()
        categoryRef.addSnapshotListener { querySnapshot, fireBaseFireStoreException ->
            fireBaseFireStoreException?.let {

                return@addSnapshotListener
            }
            querySnapshot?.let {

                for (document in it) {

                    val category: Category = document.toObject()


                    listData.add(category)
                }
                categoriesList.value = listData
            }
        }
        return categoriesList
    }

    fun getModels(collection: String): MutableLiveData<MutableList<Models>> {
        val collectionReference = Firebase.firestore.collection(collection)
        val modelData = mutableListOf<Models>()

        collectionReference.addSnapshotListener { querySnapshot, fireBaseFireStoreException ->
            fireBaseFireStoreException?.let {

                return@addSnapshotListener
            }
            querySnapshot?.let {
                for (document in it) {
                    val model: Models = document.toObject()
                    modelData.add(model)
                }
            }
        }


        return handleDocument(collection, modelData)

    }

    private fun handleDocument(collection: String, modelData: MutableList<Models>): MutableLiveData<MutableList<Models>> {


        when (collection) {
            "Furniture" -> {
                furnitureList.value = modelData

                Log.e("Model dta ", modelData.toString())
                return furnitureList
            }
            "Utensils" -> {
                utensilList.value = modelData

                return utensilList
            }
            "Clothes" -> {
                clothesList.value = modelData

                return clothesList
            }
            "Electronics" -> {
                electronicsList.value = modelData

                return electronicsList
            }
            else ->
            {
                return  furnitureList
            }


        }

    }
}
