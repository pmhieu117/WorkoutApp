package com.hieupm.btlandroid.views.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hieupm.btlandroid.R

class AdminActivity : AppCompatActivity() {
    private lateinit var fab : FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        init()
        setOnClick()
    }

    // func defind
    private fun init(){
        fab = findViewById(R.id.fab)
    }

    private fun setOnClick(){
        fab.setOnClickListener {
            val intent = Intent(this@AdminActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }
}