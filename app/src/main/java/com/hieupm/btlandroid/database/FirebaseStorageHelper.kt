package com.hieupm.btlandroid.database

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

object FirebaseStorageHelper {
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference

    fun uploadImage(imageUri: Uri, folderName: String, fileName: String, callback: (String?) -> Unit) {
        val imagesRef = storageReference.child(folderName).child(fileName)
        val uploadTask = imagesRef.putFile(imageUri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imagesRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                callback(downloadUri?.toString())
            } else {
                callback(null)
            }
        }
    }
}