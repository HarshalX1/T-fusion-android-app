package com.krish.tfusion.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.krish.tfusion.R
import com.krish.tfusion.model.Teacher
import com.krish.tfusion.ui.homeFragment.HomeFragment
import de.hdodenhof.circleimageview.CircleImageView

interface CourseClick {
    fun getCourse(currentTutor: Teacher)
}

class TeacherAdapter(private val listener: HomeFragment) :
    RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder>() {

    private var teacherList = emptyList<Teacher>()

    inner class TeacherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profilePic: CircleImageView = itemView.findViewById(R.id.ivProfilePic)
        val username: TextView = itemView.findViewById(R.id.username)
        val editPost: ImageView = itemView.findViewById(R.id.editPost)
        val subjectExpert: TextView = itemView.findViewById(R.id.subjectExpert)
        val courseExpert: TextView = itemView.findViewById(R.id.courseExpert)
        val courseBtn: Button = itemView.findViewById(R.id.courseBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.teacher_row_layout, parent, false)
        return TeacherViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeacherViewHolder, position: Int) {
        holder.apply {
            val currentTutor = teacherList[position]
            profilePic.load(currentTutor.imageUrl)
            username.text = currentTutor.username
            subjectExpert.text = currentTutor.subjectExpert
            courseExpert.text = currentTutor.courseExpert
            courseBtn.setOnClickListener {
                listener.getCourse(currentTutor)
            }
        }
    }

    override fun getItemCount(): Int = teacherList.size

    fun setData(newData: ArrayList<Teacher>) {
        teacherList = newData
        notifyDataSetChanged()
    }
}