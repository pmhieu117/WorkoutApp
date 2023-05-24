package com.hieupm.btlandroid.views.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.hieupm.btlandroid.R
import com.hieupm.btlandroid.common.Constants
import com.hieupm.btlandroid.views.activities.LoginActivity
import com.hieupm.btlandroid.views.activities.MainActivity

class SettingFragment : Fragment() {
    private lateinit var btLogout : TextView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        init(view)
        setOnClick(view)

        return view
    }

    // func defind
    private fun init(view: View){
        btLogout = view.findViewById(R.id.btnLogout)
    }
    private fun setOnClick(view: View){
        btLogout.setOnClickListener {
            Constants.AUTH.signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }
}