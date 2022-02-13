package com.krish.tfusion.utils

import com.krish.tfusion.model.Student
import com.krish.tfusion.model.Teacher
import java.util.HashMap

fun getStudentMapped(user: HashMap<*, *>): Any? {
    return Student(
        user["username"] as String?,
        user["email"] as String?,
        user["imageUrl"] as String?,
        user["uid"] as String?,
        user["collegeName"] as String?,
        user["courseEnrolled"] as String?,
        user["student"] as Boolean?
    )
}

fun getTeacherMapped(user: HashMap<*, *>): Any? {
    return Teacher(
        user["username"] as String?,
        user["email"] as String?,
        user["imageUrl"] as String?,
        user["uid"] as String?,
        user["collegeName"] as String?,
        user["subjectExpert"] as String?,
        user["courseExpert"] as String?,
        user["teacher"] as Boolean?
    )
}