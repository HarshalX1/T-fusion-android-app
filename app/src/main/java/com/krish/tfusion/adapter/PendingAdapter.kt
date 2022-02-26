package com.krish.tfusion.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.krish.tfusion.R
import com.krish.tfusion.model.Student
import com.krish.tfusion.model.Teacher
import com.krish.tfusion.model.User
import com.krish.tfusion.ui.friendFragment.PendingFragment
import de.hdodenhof.circleimageview.CircleImageView

interface AcceptDeniedRequest {
    fun acceptRequest(uid: String)
    fun deniedRequest(uid: String)
}

class PendingAdapter(private val currentUser: User, private val listener: PendingFragment) :
    RecyclerView.Adapter<PendingAdapter.PendingViewHolder>() {

    private val mAuth by lazy { FirebaseAuth.getInstance() }
    private var mStudentList = emptyList<Student>()
    private var mTutorList = emptyList<Teacher>()

    inner class PendingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // teacher
        val profilePic: CircleImageView = itemView.findViewById(R.id.ivProfilePic)
        val username: TextView = itemView.findViewById(R.id.username)

        // student
        val course: TextView = itemView.findViewById(R.id.courseEnrolled)

        val accept: ImageView = itemView.findViewById(R.id.acceptRequest)
        val denied: ImageView = itemView.findViewById(R.id.deniedRequest)
        val acceptRequest: TextView = itemView.findViewById(R.id.friendRequestAccepted)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.pending_row_layout, parent, false)
        return PendingViewHolder(view)
    }

    override fun onBindViewHolder(holder: PendingViewHolder, position: Int) {
        holder.apply {
            if (currentUser.isTeacher == true) {
                val student = mStudentList[position]
                profilePic.load(student.imageUrl)
                username.text = student.username
                course.text = student.courseEnrolled
                accept.setOnClickListener {
                    accept.visibility = View.INVISIBLE
                    denied.visibility = View.INVISIBLE
                    acceptRequest.text = "Accepted"
                    acceptRequest.setTextColor(Color.GREEN)
                    listener.acceptRequest(student.uid!!)
                }
                denied.setOnClickListener {
                    accept.visibility = View.INVISIBLE
                    denied.visibility = View.INVISIBLE
                    acceptRequest.text = "Denied"
                    acceptRequest.setTextColor(Color.RED)
                    listener.deniedRequest(student.uid!!)
                }

            } else {
                val teacher = mTutorList[position]
                profilePic.load(teacher.imageUrl)
                username.text = teacher.username
                course.text = teacher.courseExpert
                accept.setOnClickListener {
                    accept.visibility = View.INVISIBLE
                    denied.visibility = View.INVISIBLE
                    acceptRequest.text = "Accepted"
                    acceptRequest.setTextColor(Color.GREEN)
                    listener.acceptRequest(teacher.uid!!)

                }
                denied.setOnClickListener {
                    accept.visibility = View.INVISIBLE
                    denied.visibility = View.INVISIBLE
                    acceptRequest.text = "Denied"
                    acceptRequest.setTextColor(Color.RED)
                    listener.deniedRequest(teacher.uid!!)
                }
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