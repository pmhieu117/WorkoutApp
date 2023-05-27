package com.hieupm.btlandroid.views.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hieupm.btlandroid.R
import com.hieupm.btlandroid.model.Exercise
import com.hieupm.btlandroid.views.fragments.WorkoutPlayFragment

class WorkoutListExerciseAdapter (private val context: Context,private var exerciseList: List<Exercise>) : RecyclerView.Adapter<WorkoutListExerciseAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise_recyclerview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val exercise = exerciseList[position]
        holder.bind(exercise)
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvCount: TextView = itemView.findViewById(R.id.tvCount)
        private val imgExercise: ImageView = itemView.findViewById(R.id.imgExercise)

        fun bind(exercise: Exercise) {
            tvTitle.text = exercise.title
            tvCount.text = "x"+exercise.set_num.toString()
            Glide.with(itemView).asGif().load(exercise.uri_img).into(imgExercise)
            // bắt sự kiện click vào item
            itemView.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("exercise_id",exercise.id)
                val fragmentPlay = WorkoutPlayFragment()
                fragmentPlay.arguments = bundle
                val fragmentManager = (context as AppCompatActivity).supportFragmentManager
                fragmentManager.beginTransaction()
                    .replace(R.id.nav_frame, fragmentPlay)
                    .commit()
            }
        }
    }
    private fun playWorkout(context: Context, item: Exercise){

    }
}