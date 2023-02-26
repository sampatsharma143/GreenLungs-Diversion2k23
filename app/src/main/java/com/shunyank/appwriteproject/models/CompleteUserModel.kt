package com.shunyank.appwriteproject.models

data class CompleteUserModel(
    val name:String,
    val email:String,
    val age:String,
    val contactNumber:String,
    val points:Int?,
    val organisationName:String,
    val level:String

)