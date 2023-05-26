package com.hieupm.btlandroid.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
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
import com.hieupm.btlandroid.model.Favourite
import com.hieupm.btlandroid.model.User
import com.hieupm.btlandroid.views.adapters.ItemFavouriteAdapter
import com.hieupm.btlandroid.views.adapters.WorkoutListExerciseAdapter

class ReportFavouriteFragment   : Fragment() {
    private lateinit var recyclerView : RecyclerView
    private lateinit var itemFavouriteAdapter: ItemFavouriteAdapter
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
    private var fragmentView: View? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_report_favourite, container, false)
        fragmentView=view
        init(view)
        return view
    }
    private fun init(view: View){
        recyclerView = view.findViewById(R.id.ryc_favourite)
        val user = Constants.AUTH.currentUser
        user?.let {
            val uid = user.uid
            databaseReference.child("favourites").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var exerciseList : ArrayList<Exercise> = ArrayList()
                    for (postSnapshot in dataSnapshot.children) {
                        val fv_new: Favourite? = postSnapshot.getValue(Favourite::class.java)
                        if(fv_new !=null && fv_new.id != null){
                            val fv_uid = fv_new.id.substringBefore("_")
                            if (fv_uid.equals(uid)){
                                val ex_id = fv_new.id.substringAfter("_")
                                databaseReference.child("exercises/"+ex_id).addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val ex: Exercise? = snapshot.getValue(Exercise::class.java)
                                        if(ex!=null){
                                            exerciseList.add(ex)
                                        }
                                        Log.w("exerciseList",exerciseList.size.toString())
                                        itemFavouriteAdapter = ItemFavouriteAdapter(view.context,exerciseList)
                                        recyclerView.apply {
                                            layoutManager = LinearLayoutManager(view.context)
                                            adapter = itemFavouriteAdapter
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }
                                })
                            }
                        }
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
                }
            })
        }
    }

}