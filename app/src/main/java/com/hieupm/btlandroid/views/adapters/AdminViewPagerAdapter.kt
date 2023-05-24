package com.hieupm.btlandroid.views.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.hieupm.btlandroid.views.fragments.*

class AdminViewPagerAdapter (fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> AdminExerciseFragment()
            1 -> AdminExerciseFragment()
            2 -> AdminExerciseFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}