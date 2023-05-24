package com.hieupm.btlandroid.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class Exercise(
    val title : String ?=null,
    val set_num : Int ?= null,
    val set_time : Int ?= null,
    val description : String ?=null,
    val uri_img : String ?= null,
    val level_id : String ?=null
)
{
}