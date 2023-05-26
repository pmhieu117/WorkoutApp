package com.hieupm.btlandroid.views.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hieupm.btlandroid.R
import com.hieupm.btlandroid.common.Constants
import com.hieupm.btlandroid.model.User
import com.hieupm.btlandroid.views.adapters.ViewPagerAdapter

class MainActivity : AppCompatActivity() {
    //MAIN
    private lateinit var viewPager: ViewPager
    private lateinit var navigationView: BottomNavigationView
    private lateinit var fab : FloatingActionButton
    private lateinit var userLogin : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        setOnClick()
    }

    // func defind
    private fun init(){
        //MAIN
        navigationView = findViewById(R.id.bottom_nav)
        viewPager = findViewById(R.id.viewPager)
        fab = findViewById(R.id.fab)
        val adapter = ViewPagerAdapter(supportFragmentManager)
        viewPager.adapter = adapter

        val user = Constants.AUTH.currentUser
        if (user != null) {
            var database: DatabaseReference
            database = Firebase.database.reference
            val path:String = Constants.USERS+user.uid
            val userReference = database.child(path)
            val userListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI
                    val user = dataSnapshot.getValue(User::class.java)
                    // ...
                    if(user!=null && user.role.equals("admin")){
                        fab.visibility = View.VISIBLE
                    }else{
                        fab.visibility = View.GONE
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
                }
            }
            userReference.addValueEventListener(userListener)
        } else {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

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
                        navigationView.getMenu().findItem(R.id.mWorkout).setChecked(true);
                    }
                    1 -> {
                        navigationView.getMenu().findItem(R.id.mFavourite).setChecked(true);
                    }
                    2 -> {
                        navigationView.getMenu().findItem(R.id.mSetting).setChecked(true);
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
                R.id.mWorkout -> {
                    viewPager.setCurrentItem(0)
                    true
                }
                R.id.mFavourite -> {
                    viewPager.setCurrentItem(1)
                    true
                }
                R.id.mSetting -> {
                    viewPager.setCurrentItem(2)
                    true
                }
                // Xử lý các mục khác nếu cần
                else -> false
            }
        }

        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, AdminActivity::class.java)
            startActivity(intent)
        }
    }
}