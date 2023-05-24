package com.hieupm.btlandroid.common

import android.widget.ProgressBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object Constants {
    // Firebase Authentication
    val AUTH : FirebaseAuth = Firebase.auth

    // Custom Toast
    const val CUSTOM_TOAST_SUCCESS = "custom_toast_success"
    const val CUSTOM_TOAST_ERROR = "custom_toast_error"
    const val CUSTOM_TOAST_WARN = "custom_toast_warn"

    // Database Collection
    const val USERS = "users/"
    const val DIET = "diets/"
    const val EXERCISE = "exercises/"
    const val LEVEL = "levels/"
}