package com.hieupm.btlandroid.views.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.hieupm.btlandroid.R
import com.hieupm.btlandroid.views.activities.WorkoutProcessActivity

class WorkoutFragment  : Fragment() {
    private lateinit var btBeginner : Button
    private lateinit var btIntermediate : Button
    private lateinit var btAdvanced : Button
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_workout, container, false)
        init(view)
        setOnClick()
        return view
    }

    private fun init(view: View){
        btBeginner = view.findViewById(R.id.btnStartBeginner)
        btIntermediate = view.findViewById(R.id.btnStartIntermediate)
        btAdvanced = view.findViewById(R.id.btnStartAdvanced)
    }

    private fun setOnClick(){
        val intent = Intent(activity, WorkoutProcessActivity::class.java)
        btBeginner.setOnClickListener {
            intent.putExtra("LEVEL","beginner")
            startActivity(intent)
        }

        btIntermediate.setOnClickListener {
            intent.putExtra("LEVEL","intermediate")
            startActivity(intent)
        }

        btAdvanced.setOnClickListener {
            intent.putExtra("LEVEL","advanced")
            startActivity(intent)
        }

    }
}