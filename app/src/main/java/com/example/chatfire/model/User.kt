package com.example.chatfire.model

import kotlinx.android.parcel.Parcelize
import android.os.Parcelable


@Parcelize
class User (val uid: String,val username: String, val profileImageUrl:String) :Parcelable{

    constructor() : this("","","")
}