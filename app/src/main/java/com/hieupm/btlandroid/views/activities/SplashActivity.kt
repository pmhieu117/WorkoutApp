package com.hieupm.btlandroid.views.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hieupm.btlandroid.R

class SplashActivity : AppCompatActivity() {
    private val SPLASH_DELAY: Long = 3000 // Thời gian chờ 2 giây
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        auth = Firebase.auth

        Handler().postDelayed({
            checkLogin();
        }, SPLASH_DELAY)
    }

    private fun checkLogin(){
        val user = auth.currentUser
        if (user != null) {
            // User is signed in
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // No user is signed in
            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
//        val intent = Intent(this@SplashActivity, TestFacebook::class.java)
//        startActivity(intent)
//        finish()
    }
}