package com.hieupm.btlandroid.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    val email: String? = null,
    val password: String? = null,
    val fullName: String? = null,
    val birth: String? = null,
    val gender: String? = null,
    val role: String? = null
    ){

}