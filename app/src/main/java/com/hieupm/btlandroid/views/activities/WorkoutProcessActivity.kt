package com.hieupm.btlandroid.views.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.hieupm.btlandroid.R
import com.hieupm.btlandroid.common.Constants
import com.hieupm.btlandroid.custom.showCustomToast
import com.hieupm.btlandroid.views.fragments.WorkoutListExerciseFragment
import com.hieupm.btlandroid.views.fragments.WorkoutPlayFragment

class WorkoutProcessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_process)

        val fragment_workout_list_exercise = WorkoutListExerciseFragment()
        val fragment_workout_play = WorkoutPlayFragment()
        val bundle = Bundle()

        intent.getStringExtra("LEVEL")?.let {
            when (it) {
                "beginner" -> {
                    bundle.putString("level", "beginner")

                }
                "intermediate" -> {
                    bundle.putString("level", "intermediate")
                }
                "advanced" -> {
                    bundle.putString("level", "advanced")
                }
                else -> {
                    Toast(this).showCustomToast("FAIL WorkoutProcessActivity", this,Constants.CUSTOM_TOAST_ERROR)
                }
            }
        }
        fragment_workout_list_exercise.arguments = bundle
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_frame, fragment_workout_list_exercise)
            commit()
        }
    }
}