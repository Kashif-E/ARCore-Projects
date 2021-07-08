package com.infinity.modelviewerapp.ui


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import com.infinity.modelviewerapp.MainActivity
import com.infinity.modelviewerapp.R
import com.infinity.modelviewerapp.adapters.ViewPagerAdapter
import com.infinity.modelviewerapp.databinding.FragmentModelViewBinding


class ModelViewFragment : Fragment(R.layout.fragment_model_view) {

    lateinit var binding : FragmentModelViewBinding
    val args : ModelListFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentModelViewBinding.bind(view)
        val viewModel = (activity as MainActivity).activitySharedViewModel

        val fragmentList = arrayListOf(ThreeDView(), ARFragment())
        val viewPagerAdapter =
            ViewPagerAdapter(
                fragmentList,
                requireActivity().supportFragmentManager,
                lifecycle
            )

        viewModel.setModelUrl(args.model)

        binding.viewPager.adapter = viewPagerAdapter
        TabLayoutMediator(binding.tablayout, binding.viewPager
        ) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "3D"
                }
                1 -> {
                    tab.text = "AR"
                }
            }
        }.attach()
    }



}