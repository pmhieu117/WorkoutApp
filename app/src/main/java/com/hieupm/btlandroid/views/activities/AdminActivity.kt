package com.hieupm.btlandroid.views.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hieupm.btlandroid.R
import com.hieupm.btlandroid.views.adapters.AdminViewPagerAdapter
import com.hieupm.btlandroid.views.adapters.ViewPagerAdapter

class AdminActivity : AppCompatActivity() {
    private lateinit var fab : FloatingActionButton
    private lateinit var viewPager: ViewPager
    private lateinit var navigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        init()
        setOnClick()
    }

    // func defind
    private fun init(){
        navigationView = findViewById(R.id.bottom_nav)
        viewPager = findViewById(R.id.viewPager)
        fab = findViewById(R.id.fab)
        val adapter = AdminViewPagerAdapter(supportFragmentManager)
        viewPager.adapter = adapter
        fab = findViewById(R.id.fab)
    }

    private fun setOnClick(){
        val onPageChangeListener = object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                // Đang vuốt qua các Fragment
            }

            override fun onPageSelected(position: Int) {
                // Đã chuyển đến Fragment mới
                when (position) {
                    0 -> {
                        navigationView.getMenu().findItem(R.id.mExerciseAdmin).setChecked(true);
                    }
                    1 -> {
                        navigationView.getMenu().findItem(R.id.mDietAdmin).setChecked(true);
                    }
                    2 -> {
                        navigationView.getMenu().findItem(R.id.mReportAdmin).setChecked(true);
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                // Trạng thái vuốt Fragment thay đổi
            }
        }

        viewPager.addOnPageChangeListener(onPageChangeListener)

        navigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.mExerciseAdmin -> {
                    viewPager.setCurrentItem(0)
                    true
                }
                R.id.mDietAdmin -> {
                    viewPager.setCurrentItem(1)
                    true
                }
                R.id.mReportAdmin -> {
                    viewPager.setCurrentItem(2)
                    true
                }
                // Xử lý các mục khác nếu cần
                else -> false
            }
        }

        fab.setOnClickListener {
            val intent = Intent(this@AdminActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }
}