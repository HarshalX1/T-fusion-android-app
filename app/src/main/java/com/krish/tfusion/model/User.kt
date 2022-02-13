package com.krish.tfusion.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue


@Parcelize
data class User(
    var isTeacher: Boolean? = false,
    var user: @RawValue Any? = null
) : Parcelable
