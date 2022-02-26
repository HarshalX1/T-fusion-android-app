package com.krish.tfusion.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.krish.tfusion.R
import com.krish.tfusion.model.FriendRequest
import com.krish.tfusion.model.Student
import com.krish.tfusion.model.Teacher
import com.krish.tfusion.model.User
import com.krish.tfusion.ui.friendFragment.AddFragment
import de.hdodenhof.circleimageview.CircleImageView


interface AddBtnClick {
    fun sendFriendRequest(friendRequest: FriendRequest)
}

private const val TAG = "FriendsAdapter"

class FriendsAdapter(val currentUser: User, private val listener: AddFragment) :
    RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder>() {

    private val mAuth by lazy { FirebaseAuth.getInstance() }
    private var mStudentList = emptyList<Student>()
    private var mTutorList = emptyList<Teacher>()

    inner class FriendsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // teacher
        val profilePic: CircleImageView = itemView.findViewById(R.id.ivProfilePic)
        val username: TextView = itemView.findViewById(R.id.username)

        // student
        val course: TextView = itemView.findViewById(R.id.courseEnrolled)
        val addBtn: Button = itemView.findViewById(R.id.AddBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.student_row_layout, parent, false)
        return FriendsViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
        holder.apply {
            addBtn.text = "Add"
            if (currentUser.isTeacher != true) {
                val teacher = mTutorList[position]
                profilePic.load(teacher.imageUrl)
                username.text = teacher.username
                course.text = teacher.courseExpert
            } else {
                val student = mStudentList[position]
                profilePic.load(student.imageUrl)
                username.text = student.username
                course.text = student.courseEnrolled
            }
            addBtn.setOnClickListener {
                val sender = mAuth.currentUser?.uid
                if (currentUser.isTeacher != true) {
                    val teacher = mTutorList[position]
                    val receiver = teacher.uid
                    val friendRequest = FriendRequest(sender, receiver)
                    listener.sendFriendRequest(friendRequest)
                } else {
                    val student = mStudentList[position]
                    val receiver = student.uid
                    val friendRequest = FriendRequest(sender,receiver)
                    listener.sendFriendRequest(friendRequest)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (currentUser.isTeacher != true) {
            mTutorList.size
        } else {
            mStudentList.size
        }
    }

    fun setStudentData(newData: ArrayList<Student>) {
        mStudentList = newData
        Log.d(TAG, "setStudentData: $mStudentList")
        notifyDataSetChanged()
    }

    fun setTeacherData(newData: ArrayList<Teacher>) {
        mTutorList = newData
        notifyDataSetChanged()
    }


}