package com.hieupm.btlandroid.views.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.hieupm.btlandroid.R
import com.hieupm.btlandroid.common.Constants
import com.hieupm.btlandroid.custom.showCustomToast
import com.hieupm.btlandroid.database.FirebaseRealtimeHelper
import com.hieupm.btlandroid.model.Exercise
import com.hieupm.btlandroid.views.adapters.ItemExerciseAdapter
import com.squareup.picasso.Picasso
import java.util.UUID

class AdminExerciseFragment  : Fragment() {
    private lateinit var tvExName : EditText
    private lateinit var tvDesc : EditText
    private lateinit var tvSetNum : EditText
    private lateinit var tvSetTime : EditText
    private lateinit var spAdd : Spinner
    private lateinit var spFilter : Spinner

    private lateinit var btAdd : Button
    private lateinit var btSeclectImg : Button
    private lateinit var btDeleteAll : Button
    private lateinit var img : ImageView

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var mImgUri : Uri

    private lateinit var recyclerView: RecyclerView
    private lateinit var itemExerciseAdapter: ItemExerciseAdapter

    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_exercise, container, false)
        init(view)
        setOnClick(requireActivity())
        displayRecyclerView(view)
        return view
    }

    private fun init(view: View){
        tvExName = view.findViewById(R.id.tvExName)
        tvDesc = view.findViewById(R.id.tvDesc)
        tvSetNum = view.findViewById(R.id.tvSetNum)
        tvSetTime = view.findViewById(R.id.tvSetTime)

        btAdd = view.findViewById(R.id.btnAdd)
        btSeclectImg = view.findViewById(R.id.btSelectImg)
        btDeleteAll = view.findViewById(R.id.btnDeleteAll)
        img = view.findViewById(R.id.imgDisplay)

        spAdd = view.findViewById(R.id.spAdd)
        val adapterAdd = ArrayAdapter<String>(view.context,android.R.layout.simple_spinner_item,resources.getStringArray(R.array.levels))
        adapterAdd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spAdd.adapter = adapterAdd

        spFilter = view.findViewById(R.id.spFilter)
        val adapterFilter = ArrayAdapter<String>(view.context,android.R.layout.simple_spinner_item,resources.getStringArray(R.array.level_filter))
        adapterFilter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spFilter.adapter = adapterFilter
        spFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = spFilter.selectedItem.toString()
                var new_data : ArrayList<Exercise> = ArrayList()
                databaseReference.child("exercises").addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (postSnapshot in dataSnapshot.children) {
                            val ex_new: Exercise? = postSnapshot.getValue(Exercise::class.java)
                            if(ex_new !=null){
                                new_data.add(ex_new)
                            }
                        }
                        Log.e("TAG","OK")
                        if(selectedItem.equals("All")){
                            Log.e("TAG",new_data.size.toString())
                            itemExerciseAdapter.setData(new_data)
                        }else if(selectedItem.equals("Beginner")){
                            Log.e("TAG","Beginner")
                            itemExerciseAdapter.setData(new_data.filter { it.level_id == "beginner" })
                        }else if(selectedItem.equals("Intermediate")){
                            Log.e("TAG","Intermediate")
                            itemExerciseAdapter.setData(new_data.filter { it.level_id == "intermediate" })
                        }else if(selectedItem.equals("Advanced")){
                            Log.e("TAG","Advanced")
                            itemExerciseAdapter.setData(new_data.filter { it.level_id == "advanced" })
                        }else{
                            Log.e("TAG","122")
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Getting Post failed, log a message
                        Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
                        // ...
                    }
                })
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Xử lý khi không có mục nào được chọn
            }
        }
    }

    private fun setOnClick(activity: Activity){
        btAdd.setOnClickListener {
            val exName = tvExName.text.toString().trim()
            val desc = tvDesc.text.toString().trim()
            var setNum = 0
            if(tvSetNum.text.toString().trim() != null && !tvSetNum.text.toString().trim().equals("")) setNum = tvSetNum.text.toString().trim().toInt()
            var setTime = 0
            if(tvSetTime.text.toString().trim() != null && !tvSetTime.text.toString().trim().equals("")) setTime = tvSetTime.text.toString().trim().toInt()
            val levelId = resources.getStringArray(R.array.levels)[spAdd.selectedItemPosition].trim().lowercase()
            if(exName == null || exName.equals("")){
                Toast(activity).showCustomToast("Exercise name is not empty", activity, Constants.CUSTOM_TOAST_WARN)
            }else if(desc == null || desc.equals("")){
                Toast(activity).showCustomToast("Description is not empty", activity, Constants.CUSTOM_TOAST_WARN)
            }else if(mImgUri == null){
                Toast(activity).showCustomToast("Image is not empty", activity, Constants.CUSTOM_TOAST_WARN)
            }else if(levelId == null || levelId.equals("")){
                Toast(activity).showCustomToast("Level name is not empty", activity, Constants.CUSTOM_TOAST_WARN)
            }else {
                val imageRef = storageReference.child("images/${UUID.randomUUID()}.jpg")
                // Tải ảnh lên Firebase Storage
                val uploadTask = imageRef.putFile(mImgUri)
                // Lắng nghe sự kiện tải lên thành công hoặc thất bại
                uploadTask.addOnSuccessListener {
                    // Ảnh đã được tải lên thành công, bạn có thể lấy URL để lưu vào Realtime Database hoặc Firestore
                    imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        val uriImg = downloadUrl.toString()
                        var path = "exercises"
                        val key = databaseReference.child(path).push().key
                        val exercise = Exercise(key,exName, setNum, setTime, desc, uriImg, levelId)
                        path+="/${key}"
                        FirebaseRealtimeHelper(activity).addObject(exercise, path)
                    }
                }.addOnFailureListener { exception ->
                    // Xử lý khi tải lên ảnh thất bại
                    Log.e("UPLOAD_IMG","FAIL")
                }
            }

        }

        btSeclectImg.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        btDeleteAll.setOnClickListener {
            tvExName.text = null
            tvDesc.text = null
            tvSetNum.text = null
            tvSetTime.text = null
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("Picasso",requestCode.toString())
        Log.e("Picasso",requestCode.toString())
        if(requestCode == PICK_IMAGE_REQUEST && requestCode == 1 &&
            data != null && data.data != null ){
            Log.e("Picasso","OK")
            mImgUri = data.data!!
            img.setBackgroundResource(0)
            Picasso.get().load(mImgUri).into(img)
        }else{
            Log.e("Picasso","KO")
        }
    }
    private fun displayRecyclerView(view: View){
        recyclerView = view.findViewById(R.id.recycleViewListEx)
        var exerciseList : ArrayList<Exercise> = ArrayList()
        databaseReference.child("exercises").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val exercise : Exercise? = dataSnapshot.getValue<Exercise>()
                if(exercise!=null){
                    exerciseList.add(exercise)
                    itemExerciseAdapter.notifyDataSetChanged()
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val exercise : Exercise? = dataSnapshot.getValue<Exercise>()
                if(exercise==null || exerciseList == null || exerciseList.isEmpty()){
                    return
                }else{
                    for (i in 0 until exerciseList.size){
                        if(exercise.id.equals(exerciseList.get(i).id) == true){
                            exerciseList.set(i,exercise)
                            itemExerciseAdapter.notifyDataSetChanged()
                            break
                        }
                    }
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val exercise : Exercise? = dataSnapshot.getValue<Exercise>()
                if(exercise==null || exerciseList == null || exerciseList.isEmpty()){
                    return
                }else{
                    for (i in 0 until exerciseList.size){
                        if(exercise.id.equals(exerciseList.get(i).id) == true){
                            exerciseList.remove(exerciseList.get(i))
                            itemExerciseAdapter.notifyDataSetChanged()
                            break
                        }
                    }
                }
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    context,
                    "Failed to load exercises.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        })

        val levelFilter = resources.getStringArray(R.array.level_filter)[spFilter.selectedItemPosition].trim().lowercase()
        Log.e("LEVEL_FILTER",levelFilter)
        if(levelFilter.equals("all")){
            itemExerciseAdapter = ItemExerciseAdapter(exerciseList)
        }else{
            itemExerciseAdapter = ItemExerciseAdapter(exerciseList.filter { it.level_id == levelFilter })
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = itemExerciseAdapter
        }
    }

}