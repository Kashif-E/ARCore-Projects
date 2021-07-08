package com.infinity.modelviewerapp.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Session
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException
import com.infinity.modelviewerapp.MainActivity
import com.infinity.modelviewerapp.R
import com.infinity.modelviewerapp.adapters.CategoryAdapter
import com.infinity.modelviewerapp.databinding.FragmentCategoryBinding
import com.infinity.modelviewerapp.util.CameraPermissionHelper
import java.util.*


class CategoryFragment : Fragment(R.layout.fragment_category) {

    lateinit var categoryAdapter : CategoryAdapter
    lateinit var  sharedViewModels : SharedViewModel
    lateinit var binding : FragmentCategoryBinding
    var mSession: Session? = null
    private   var M_USER_REQUEST_INSTALL = true
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCategoryBinding.bind(view)
        categoryAdapter= CategoryAdapter()
        sharedViewModels = (activity as MainActivity).activitySharedViewModel

        categoryAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putString("model",it.CategoryName)
            }
            findNavController().navigate(R.id.action_categoryFragment_to_modelListFragment,bundle)
        }
        binding.rvCategory.adapter= categoryAdapter

        sharedViewModels.category.observe(viewLifecycleOwner, { response ->

            categoryAdapter.differ.submitList(response.toList())
        })

    }




}