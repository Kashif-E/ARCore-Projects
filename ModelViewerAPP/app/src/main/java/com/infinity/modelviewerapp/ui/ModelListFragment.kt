package com.infinity.modelviewerapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Session
import com.google.ar.core.SharedCamera
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException
import com.infinity.modelviewerapp.MainActivity
import com.infinity.modelviewerapp.R
import com.infinity.modelviewerapp.adapters.ModelsAdapter
import com.infinity.modelviewerapp.databinding.FragmentModelListBinding
import com.infinity.modelviewerapp.util.CameraPermissionHelper
import java.util.*


class ModelListFragment : Fragment() {

    lateinit var binding : FragmentModelListBinding
    lateinit var  viewModel: SharedViewModel
    lateinit var adapter : ModelsAdapter



    val args : ModelListFragmentArgs by navArgs()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_model_list, container, false)

        adapter= ModelsAdapter()

        viewModel= (activity as MainActivity).activitySharedViewModel
        binding.rvCategory.adapter= adapter
        Toast.makeText(requireContext(), args.model,Toast.LENGTH_LONG).show()
        when(args.model)
        {
            "Furniture" ->
            {

                viewModel.furniture.observe(viewLifecycleOwner, {response ->


                    adapter.differ.submitList(response.toList())
                })
            }
            "Clothes" ->
            {

                viewModel.clothes.observe(viewLifecycleOwner, {response ->


                    adapter.differ.submitList(response.toList())
                })
            }
            "Electronics" ->
            {

                viewModel.electronics.observe(viewLifecycleOwner, {response ->


                    adapter.differ.submitList(response.toList())
                })

            }
            "Utensils" ->
            {

                viewModel.utensils.observe(viewLifecycleOwner, {response ->


                    adapter.differ.submitList(response.toList())
                })
            }

            else->
            {
                Toast.makeText(requireContext(), "not working ",Toast.LENGTH_LONG).show()
            }
        }



        adapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putString("model",it.Model)
            }
            findNavController().navigate(R.id.action_modelListFragment_to_modelViewFragment,bundle)
        }



        return  binding.root
    }





}



