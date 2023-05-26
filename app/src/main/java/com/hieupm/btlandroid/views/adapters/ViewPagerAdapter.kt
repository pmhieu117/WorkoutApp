package com.hieupm.btlandroid.views.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.hieupm.btlandroid.views.fragments.ReportFavouriteFragment
import com.hieupm.btlandroid.views.fragments.SettingFragment
import com.hieupm.btlandroid.views.fragments.WorkoutFragment

class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> WorkoutFragment()
            1 -> ReportFavouriteFragment()
            2 -> SettingFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}