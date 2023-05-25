package com.hieupm.btlandroid.views.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.hieupm.btlandroid.R
import com.hieupm.btlandroid.common.Constants
import com.hieupm.btlandroid.database.FirebaseDatabaseHelper
import com.hieupm.btlandroid.model.Exercise
import java.text.SimpleDateFormat
import java.util.Date

class WorkoutPlayFragment : Fragment() {
    private val handler = Handler()
    private var delayTime: Long = 3000 // 3 giây
    private var mediaPlayer: MediaPlayer? = null
    private var isMusicPlaying = false
    private var isFavoutite = false

    private lateinit var id_ex : String
    private lateinit var imgExercise : ImageView
    private lateinit var tvExerciseName : TextView
    private lateinit var tvDesc : TextView
    private lateinit var btSound : ImageView
    private lateinit var btnFavourite : ImageView

    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_workout_play, container, false)
        init(view)
        setOnClick()
        startMusic()
        val bundle = arguments
        if(bundle!=null) {
            id_ex = bundle.getString("exercise_id").toString()
            val path : String = Constants.EXERCISE+id_ex
            databaseReference.child(path).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI
                    val ex:Exercise ?= dataSnapshot.getValue(Exercise::class.java)
                    if(ex!=null){
                        tvExerciseName.text = ex.title
                        tvDesc.text = ex.description
                        Glide.with(view).asGif().load(ex.uri_img).into(imgExercise)
                        delayTime = (ex.set_time?.toLong() ?: 1) * 1000
                        handler.postDelayed({
                            // Chạy Fragment mới
                            val bundle = Bundle()
                            bundle.putString("level", ex.level_id)
                            val fragmentB = WorkoutListExerciseFragment()
                            val fragmentManager = requireActivity().supportFragmentManager
                            fragmentB.arguments = bundle
                            fragmentManager.beginTransaction()
                                .replace(R.id.nav_frame, fragmentB)
                                .commit()
                        }, delayTime)
                    }
                    // ...
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
                }
            })
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun init(view: View){
        imgExercise = view.findViewById(R.id.imgExercise)
        tvExerciseName = view.findViewById(R.id.tvExerciseName)
        tvDesc = view.findViewById(R.id.tvDesc)
        btSound = view.findViewById(R.id.btnSound)
        btnFavourite = view.findViewById(R.id.btnFavourite)
        val user = Firebase.auth.currentUser
        if (user != null) {
            val bundle = arguments
            if(bundle!=null){
                val path : String = Constants.FAVOURITE+user.uid+"_"+bundle.getString("exercise_id").toString()
                FirebaseDatabaseHelper.checkKeyExists(path, callback = {
                    if(it){
                        isFavoutite=true
                        btnFavourite.setImageResource(R.drawable.ic_favourited)
                    }else{
                        isFavoutite=false
                        btnFavourite.setImageResource(R.drawable.ic_favourite)
                    }
                })
            }
        }
    }

    private fun setOnClick(){
        btSound.setOnClickListener {
            toggleMusic()
        }

        btnFavourite.setOnClickListener {
            if(isFavoutite){
                val user = Firebase.auth.currentUser
                if (user != null) {
                    val bundle = arguments
                    if(bundle!=null){
                        val path : String = Constants.FAVOURITE+user.uid+"_"+bundle.getString("exercise_id").toString()
                        FirebaseDatabaseHelper.deleteObject(path)
                        isFavoutite=false
                        btnFavourite.setImageResource(R.drawable.ic_favourite)
                    }
                }
            }else{
                val user = Firebase.auth.currentUser
                if (user != null) {
                    val bundle = arguments
                    if(bundle!=null){
                        val currentTime = Date()
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        val formattedTime = dateFormat.format(currentTime)
                        val path : String = Constants.FAVOURITE+user.uid+"_"+bundle.getString("exercise_id").toString()+"/time"
                        databaseReference.child(path).setValue(formattedTime)
                        isFavoutite=true
                        btnFavourite.setImageResource(R.drawable.ic_favourited)
                    }
                }
            }
        }
    }
    private fun toggleMusic() {
        if (isMusicPlaying) {
            stopMusic()
        } else {
            startMusic()
        }
    }

    private fun startMusic() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(requireContext(), R.raw.play_background)
            mediaPlayer?.setOnPreparedListener { player ->
                player.start()
                isMusicPlaying = true

            }
        }
    }

    private fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        isMusicPlaying = false
    }
}