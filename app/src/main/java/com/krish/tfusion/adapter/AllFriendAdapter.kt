package com.krish.tfusion.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.krish.tfusion.R
import com.krish.tfusion.model.Student
import com.krish.tfusion.model.Teacher
import com.krish.tfusion.model.User
import de.hdodenhof.circleimageview.CircleImageView

class AllFriendAdapter(private val currentUser : User) :
    RecyclerView.Adapter<AllFriendAdapter.AllFriendViewHolder>() {

    private var mStudentList = emptyList<Student>()
    private var mTutorList = emptyList<Teacher>()

    inner class AllFriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // teacher
        val profilePic: CircleImageView = itemView.findViewById(R.id.ivProfilePic)
        val username: TextView = itemView.findViewById(R.id.username)

        // student
        val course: TextView = itemView.findViewById(R.id.courseEnrolled)
        val emailId: TextView = itemView.findViewById(R.id.emailId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllFriendViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.all_friend_row_layout, parent, false)
        return AllFriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: AllFriendViewHolder, position: Int) {
        holder.apply {
            if (currentUser.isTeacher == true)
            {
                val student = mStudentList[position]
                profilePic.load(student.imageUrl)
                username.text = student.username
                course.text = student.courseEnrolled
                emailId.text = student.email
            }else{
                val teacher = mTutorList[position]
                profilePic.load(teacher.imageUrl)
                username.text = teacher.username
                course.text = teacher.courseExpert
                emailId.text = teacher.email
            }
        }
    }

    override fun getItemCount(): Int {
        return if (currentUser.isTeacher == true) {
            mStudentList.size
        } else {
            mTutorList.size
        }
    }

    fun getStudent(newData: ArrayList<Student>) {
        mStudentList = newData
        notifyDataSetChanged()
    }

    fun getTeacher(newData: ArrayList<Teacher>) {
        mTutorList = newData
        notifyDataSetChanged()
    }

}