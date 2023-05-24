package com.hieupm.btlandroid.views.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.hieupm.btlandroid.views.fragments.DietFragment
import com.hieupm.btlandroid.views.fragments.ReportFragment
import com.hieupm.btlandroid.views.fragments.SettingFragment
import com.hieupm.btlandroid.views.fragments.WorkoutFragment

class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 4
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> WorkoutFragment()
            1 -> ReportFragment()
            2 -> DietFragment()
            3 -> SettingFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}