package com.ridhotegar.androidsub2.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ridhotegar.androidsub2.R
import com.ridhotegar.androidsub2.ui.follow.FollowFragment

class SectionPagerAdapter(private val fa: FragmentActivity) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int {
        return fa.resources.getStringArray(R.array.tabs).size
    }

    override fun createFragment(position: Int): Fragment {
        return FollowFragment.getInstance(position)
    }

}