package com.hieupm.btlandroid.views.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hieupm.btlandroid.R
import com.hieupm.btlandroid.common.Constants
import com.hieupm.btlandroid.custom.showCustomToast
import com.hieupm.btlandroid.database.FirebaseDatabaseHelper
import com.hieupm.btlandroid.model.User
import com.hieupm.btlandroid.views.activities.LoginActivity
import com.hieupm.btlandroid.views.activities.MainActivity

class SettingFragment : Fragment() {
    private lateinit var email : TextView
    //private lateinit var password : TextView
    private lateinit var fullname : TextView
    private lateinit var birth : TextView
    private lateinit var gender : TextView

    private lateinit var btLogout : TextView
    private lateinit var btn_update:Button
    private lateinit var btnChangePass:TextView

    private var database: DatabaseReference = Firebase.database.reference
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        init(view)
        setOnClick(requireActivity())
        return view
    }

    override fun onResume() {
        super.onResume()
        Log.w("exerciseList","SettingFragment")
        displayProfile()
    }

    // func defind
    private fun init(view: View){
        email = view.findViewById(R.id.email)
//        password = view.findViewById(R.id.password)
        fullname = view.findViewById(R.id.fullname)
        birth = view.findViewById(R.id.birth)
        gender = view.findViewById(R.id.gender)
        btn_update = view.findViewById(R.id.btn_update)
        btLogout = view.findViewById(R.id.btnLogout)
        btnChangePass = view.findViewById(R.id.btnChangePass)
    }
    private fun setOnClick(activity: Activity){
        btLogout.setOnClickListener {
            Constants.AUTH.signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
        btn_update.setOnClickListener {
            if(requireActivity().intent.hasExtra("GOOGLE")==true){
                Toast(activity).showCustomToast("You can't update while logged in with Google", activity, Constants.CUSTOM_TOAST_ERROR)
            }else {
                val user = Constants.AUTH.currentUser
                user?.let {
                    val uid = user.uid
                    val path: String = Constants.USERS + uid
                    if (!user.email.equals(email.text.toString().trim())) {
                        val user = Constants.AUTH.currentUser
                        val email_new: String = email.text.toString().trim()
                        user!!.updateEmail(email_new)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val updates = HashMap<String, Any>()
                                    updates["email"] = email.text.toString().trim()
                                    updates["fullName"] = fullname.text.toString().trim()
                                    updates["birth"] = birth.text.toString().trim()
                                    updates["gender"] = gender.text.toString().trim()
                                    database.child(path).updateChildren(updates)
                                        .addOnSuccessListener {
                                            Toast(activity).showCustomToast(
                                                "Update Profile Success",
                                                activity,
                                                Constants.CUSTOM_TOAST_SUCCESS
                                            )
                                            Constants.AUTH.signOut()
                                            val intent = Intent(activity, LoginActivity::class.java)
                                            startActivity(intent)
                                            activity?.finish()
                                        }
                                        .addOnFailureListener {
                                            Toast(activity).showCustomToast(
                                                it.message.toString(),
                                                activity,
                                                Constants.CUSTOM_TOAST_ERROR
                                            )
                                        }
                                } else {
                                    Log.w("TAG", task.exception.toString())
                                    Toast(activity).showCustomToast(
                                        "Update Profile Fail",
                                        activity,
                                        Constants.CUSTOM_TOAST_ERROR
                                    )
                                }
                            }
                    } else {
                        val updates = HashMap<String, Any>()
                        updates["fullName"] = fullname.text.toString().trim()
                        updates["birth"] = birth.text.toString().trim()
                        updates["gender"] = gender.text.toString().trim()
                        database.child(path).updateChildren(updates)
                            .addOnSuccessListener {
                                Toast(activity).showCustomToast(
                                    "Update Profile Success",
                                    activity,
                                    Constants.CUSTOM_TOAST_SUCCESS
                                )
                            }
                            .addOnFailureListener {
                                Toast(activity).showCustomToast(
                                    "Update Profile Fail",
                                    activity,
                                    Constants.CUSTOM_TOAST_ERROR
                                )
                            }
                    }
                }
            }
        }

        btnChangePass.setOnClickListener {
            if (requireActivity().intent.hasExtra("GOOGLE") == true) {
                Toast(activity).showCustomToast(
                    "You can't update while logged in with Google",
                    activity,
                    Constants.CUSTOM_TOAST_WARN
                )
            } else {
                val dialogView =
                    LayoutInflater.from(context).inflate(R.layout.dialog_change_password, null)
                val builder = AlertDialog.Builder(context)
                    .setView(dialogView)
                val dialog = builder.create()

                val btnUpdate = dialogView.findViewById<Button>(R.id.btnUpdate)
                val btnDelete = dialogView.findViewById<Button>(R.id.btnDelete)
                val password = dialogView.findViewById<EditText>(R.id.password)
                val verPassword = dialogView.findViewById<EditText>(R.id.verPassword)
                val newPassword = dialogView.findViewById<EditText>(R.id.newPassword)

                btnUpdate.setOnClickListener {
                    val pass = password.text.toString().trim()
                    val new_pass = newPassword.text.toString().trim()
                    val ver_pass = verPassword.text.toString().trim()
                    if (pass.equals(new_pass)) {
                        Toast(activity).showCustomToast(
                            "New password duplicated.",
                            activity,
                            Constants.CUSTOM_TOAST_ERROR
                        )
                    } else if (new_pass.equals(ver_pass) == false) {
                        Toast(activity).showCustomToast(
                            "Verification failed.",
                            activity,
                            Constants.CUSTOM_TOAST_ERROR
                        )
                    } else {
                        val user = Constants.AUTH.currentUser
                        user?.let {
                            val uid = user.uid
                            val path: String = Constants.USERS + uid
                            database.child(path).addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    val user_info: User? = dataSnapshot.getValue(User::class.java)
                                    if (user_info != null) {
                                        if (pass.equals(user_info.password) == false) {
                                            Toast(activity).showCustomToast(
                                                "Current password is wrong",
                                                activity,
                                                Constants.CUSTOM_TOAST_ERROR
                                            )
                                        } else {
                                            user!!.updatePassword(new_pass)
                                                .addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        val updates = HashMap<String, Any>()
                                                        updates["password"] = new_pass
                                                        database.child(path).updateChildren(updates)
                                                            .addOnSuccessListener {
                                                                Toast(activity).showCustomToast(
                                                                    "Change password success",
                                                                    activity,
                                                                    Constants.CUSTOM_TOAST_SUCCESS
                                                                )
                                                            }
                                                            .addOnFailureListener {
                                                                Toast(activity).showCustomToast(
                                                                    "Change password failed",
                                                                    activity,
                                                                    Constants.CUSTOM_TOAST_ERROR
                                                                )
                                                            }
                                                    } else {
                                                        Log.e("TAG", task.exception.toString())
                                                        Toast(activity).showCustomToast(
                                                            "Change password failed",
                                                            activity,
                                                            Constants.CUSTOM_TOAST_ERROR
                                                        )
                                                    }
                                                }
                                        }
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    // Getting Post failed, log a message
                                    Log.w(
                                        "TAG",
                                        "loadPost:onCancelled",
                                        databaseError.toException()
                                    )
                                }
                            })

                        }
                    }
                    dialog.dismiss()
                }
                btnDelete.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show()
            }
        }
    }

    private fun displayProfile(){
        val user = Constants.AUTH.currentUser
        if(requireActivity().intent.hasExtra("GOOGLE")==true){
            user?.let {
                email.text = user.email
                fullname.text = user.displayName
            }
        }else {
            user?.let {
                val uid = user.uid
                database.child(Constants.USERS + uid)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val user_info: User? = dataSnapshot.getValue(User::class.java)
                            if (user_info != null) {
                                email.text = user_info.email
                                //password.text = user_info.password
                                fullname.text = user_info.fullName
                                birth.text = user_info.birth
                                gender.text = user_info.gender
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Getting Post failed, log a message
                            Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
                        }
                    })
            }
        }
    }
}