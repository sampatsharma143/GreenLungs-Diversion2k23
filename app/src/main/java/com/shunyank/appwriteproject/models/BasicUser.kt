package com.shunyank.appwriteproject.models

data class BasicUser(
    val id:String,
    val name:String,
    val email:String,
    var org:String?
){
    fun setOrgnization(org:String?):BasicUser{
        this.org = org
        return this
    }
}
