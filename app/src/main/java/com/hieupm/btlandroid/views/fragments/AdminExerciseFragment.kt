package com.hieupm.btlandroid.views.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.hieupm.btlandroid.R
import com.hieupm.btlandroid.common.Constants
import com.hieupm.btlandroid.custom.showCustomToast
import com.hieupm.btlandroid.database.FirebaseRealtimeHelper
import com.hieupm.btlandroid.model.Exercise
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
    private lateinit var img : ImageView

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var mImgUri : Uri

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
        return view
    }

    private fun init(view: View){
        tvExName = view.findViewById(R.id.tvExName)
        tvDesc = view.findViewById(R.id.tvDesc)
        tvSetNum = view.findViewById(R.id.tvSetNum)
        tvSetTime = view.findViewById(R.id.tvSetTime)

        btAdd = view.findViewById(R.id.btnAdd)
        btSeclectImg = view.findViewById(R.id.btSelectImg)
        img = view.findViewById(R.id.imgDisplay)

        spAdd = view.findViewById(R.id.spAdd)
        val adapterAdd = ArrayAdapter<String>(view.context,android.R.layout.simple_spinner_item,resources.getStringArray(R.array.levels))
        adapterAdd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spAdd.adapter = adapterAdd

        spFilter = view.findViewById(R.id.spFilter)
        val adapterFilter = ArrayAdapter<String>(view.context,android.R.layout.simple_spinner_item,resources.getStringArray(R.array.levels))
        adapterFilter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spFilter.adapter = adapterFilter
    }

    private fun setOnClick(activity: Activity){
        btAdd.setOnClickListener {
            val exName = tvExName.text.toString().trim()
            val desc = tvDesc.text.toString().trim()
            val setNum = tvSetNum.text.toString().trim().toInt()
            val setTime = tvSetTime.text.toString().trim().toInt()
            val levelId = resources.getStringArray(R.array.levels)[spAdd.selectedItemPosition].trim().lowercase()
            if(exName == null || exName.equals("")){
                Toast(activity).showCustomToast("Exercise name is not empty", activity, Constants.CUSTOM_TOAST_WARN)
            }else if(desc == null || desc.equals("")){
                Toast(activity).showCustomToast("Description is not empty", activity, Constants.CUSTOM_TOAST_WARN)
            }else if(setNum == null || setNum.equals("")){
                Toast(activity).showCustomToast("Set number name is not empty", activity, Constants.CUSTOM_TOAST_WARN)
            }else if(setTime == null || setTime.equals("")){
                Toast(activity).showCustomToast("Set time name is not empty", activity, Constants.CUSTOM_TOAST_WARN)
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
                        val exercise = Exercise(exName, setNum, setTime, desc, uriImg, levelId)
                        val path = "exercises"
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
}