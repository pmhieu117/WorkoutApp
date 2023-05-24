package com.hieupm.btlandroid.database

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object FirebaseDatabaseHelper {
    private val database: DatabaseReference = Firebase.database.reference

    fun addObject(path: String, obj: Any) {
        database.child(path).setValue(obj)
    }

    fun updateObject(path: String,obj: Any ) {
        val childUpdates = HashMap<String, Any>()
        childUpdates[path] = obj
        database.updateChildren(childUpdates)
    }

    fun deleteObject(path: String) {
        database.child(path).removeValue()
    }

    fun getObjects(path: String, listener: ValueEventListener) {
        database.child(path).addListenerForSingleValueEvent(listener)
    }

    fun checkKeyExists(path: String, callback: (Boolean) -> Unit){
        val databaseS = Firebase.database
        val reference = databaseS.getReference(path)
        // hàm addListenerForSingleValueEvent là 1 hàm bất đồng bộ và nó sẽ chạy 1 luồng khác
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    callback(true)
                } else {
                    callback(false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(true)
            }
        })
    }
}