package com.hieupm.btlandroid.views.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.hieupm.btlandroid.views.fragments.*

class ReportViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 3;    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ReportTotalFragment()
            1 -> ReportHistoryFragment()
            2 -> ReportFavouriteFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "TOTAL"
            1 -> "HISTORY"
            2 -> "FAVOURITE"
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}