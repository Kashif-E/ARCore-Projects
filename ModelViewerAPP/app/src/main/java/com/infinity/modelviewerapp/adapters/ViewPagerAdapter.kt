package com.infinity.modelviewerapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter


class ViewPagerAdapter( val fragmentlist : ArrayList<Fragment>,fragmentManager: FragmentManager ,lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager ,lifecycle) {
        override fun getItemCount(): Int {

            return fragmentlist.size
        }

        override fun createFragment(position: Int): Fragment {
            return  fragmentlist[position]
        }

}

