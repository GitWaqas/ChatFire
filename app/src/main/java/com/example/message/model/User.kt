package com.example.message.model

data class User (val name:String,val bio:String,val avatarPath:String?,
                 val registrationTokens:MutableList<String>){
    constructor():this("","",null, mutableListOf())
}