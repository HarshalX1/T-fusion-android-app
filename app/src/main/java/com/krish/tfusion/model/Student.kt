package com.krish.tfusion.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Student(
    var username : String? = null,
    var email : String? = null,
    var imageUrl : String? = null,
    var uid : String? = null,
    var collegeName: String? = null,
    var courseEnrolled:String? = null,
    var student : Boolean? =  false
):Parcelable