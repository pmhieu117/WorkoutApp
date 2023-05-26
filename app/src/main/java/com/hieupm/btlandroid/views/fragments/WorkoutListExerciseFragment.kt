package com.hieupm.btlandroid.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.hieupm.btlandroid.R
import com.hieupm.btlandroid.common.Constants
import com.hieupm.btlandroid.custom.showCustomToast
import com.hieupm.btlandroid.model.Exercise
import com.hieupm.btlandroid.views.adapters.ItemExerciseAdapter
import com.hieupm.btlandroid.views.adapters.WorkoutListExerciseAdapter

class WorkoutListExerciseFragment : Fragment() {
    private lateinit var frameLayoutIntro : FrameLayout
    private lateinit var tvLevel : TextView
    private lateinit var tvTime : TextView
    private lateinit var tvWorkouts : TextView
    //private lateinit var tvFavourite : TextView

    private lateinit var recyclerView : RecyclerView
    private lateinit var workoutListExerciseAdapter: WorkoutListExerciseAdapter

    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_workout_list_exercise, container, false)
        init(view)
        val bundle = arguments
        if(bundle!=null){
            bundle.getString("level")?.let {
                when (it) {
                    "beginner" -> {
                        frameLayoutIntro.setBackgroundResource(R.drawable.beginner)
                        tvLevel.text= "BEGINNER"
                        //tvFavourite.text= "0"

                    }
                    "intermediate" -> {
                        frameLayoutIntro.setBackgroundResource(R.drawable.intermediate)
                        tvLevel.text= "INTERMEDIATE"
                        //tvFavourite.text= "0"
                    }
                    "advanced" -> {
                        frameLayoutIntro.setBackgroundResource(R.drawable.advanced)
                        tvLevel.text= "ADVANCED"
                        //tvFavourite.text= "0"
                    }
                    else -> {
                        Toast(view.context).showCustomToast("FAIL WorkoutProcessActivity", requireActivity(), Constants.CUSTOM_TOAST_ERROR)
                    }
                }
                displayAdapter(view,it);
            }

        }
        return view
    }

    private fun init(view : View){
        frameLayoutIntro = view.findViewById(R.id.intro_frame)
        tvLevel = view.findViewById(R.id.tvLevel)
        tvTime = view.findViewById(R.id.tvTime)
        tvWorkouts = view.findViewById(R.id.tvWorkouts)
        //tvFavourite = view.findViewById(R.id.tvFavourite)
    }

    private fun displayAdapter(view: View,level: String){
        recyclerView = view.findViewById(R.id.ryc_exercise)
        var exerciseList : ArrayList<Exercise> = ArrayList()
        databaseReference.child("exercises").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {
                    val ex_new: Exercise? = postSnapshot.getValue(Exercise::class.java)
                    if(ex_new !=null){
                        exerciseList.add(ex_new)
                    }
                }

                tvTime.text=exerciseList.filter { it.level_id == level }.sumOf{ if(it.set_time!= null ) it.set_time else 0 }.toString()
                tvWorkouts.text = exerciseList.filter { it.level_id == level }.size.toString()
                workoutListExerciseAdapter = WorkoutListExerciseAdapter(view.context,exerciseList.filter { it.level_id == level })
                recyclerView.apply {
                    layoutManager = LinearLayoutManager(view.context)
                    adapter = workoutListExerciseAdapter
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        })

    }
}