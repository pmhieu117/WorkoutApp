package com.hieupm.btlandroid.database

import android.app.Activity
import android.view.View
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hieupm.btlandroid.common.Constants
import com.hieupm.btlandroid.custom.showCustomToast
import com.hieupm.btlandroid.model.Exercise

class FirebaseRealtimeHelper(private val activity: Activity) {

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun addObject(obj: Any, path: String) {
        val childUpdates = HashMap<String, Any>()
        childUpdates["$path"] = obj
        databaseReference.updateChildren(childUpdates)
            .addOnSuccessListener {
                Toast(activity).showCustomToast("Add Success !", activity, Constants.CUSTOM_TOAST_SUCCESS)
            }
            .addOnFailureListener {
                Toast(activity).showCustomToast("Add Fail !", activity, Constants.CUSTOM_TOAST_ERROR)
            }
    }

    fun updateObject(obj: Any, path: String) {
        val childUpdates = HashMap<String, Any>()
        childUpdates[path] = obj
        databaseReference.updateChildren(childUpdates)
            .addOnSuccessListener {
                Toast(activity).showCustomToast("Update Success !", activity, Constants.CUSTOM_TOAST_SUCCESS)
            }
            .addOnFailureListener {
                Toast(activity).showCustomToast("Update Fail !", activity, Constants.CUSTOM_TOAST_ERROR)
            }
    }

    fun deleteObject(path: String) {
        databaseReference.child(path).removeValue()
            .addOnSuccessListener {
                Toast(activity).showCustomToast("Add Success !", activity, Constants.CUSTOM_TOAST_SUCCESS)
            }
            .addOnFailureListener {
                Toast(activity).showCustomToast("Add Fail !", activity, Constants.CUSTOM_TOAST_ERROR)
            }
    }

    fun getObjects(path: String, listener: ValueEventListener) {
        databaseReference.child(path).addListenerForSingleValueEvent(listener)
    }
}