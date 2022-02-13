package com.krish.tfusion.ui.homeFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.krish.tfusion.adapter.CourseClick
import com.krish.tfusion.adapter.TeacherAdapter
import com.krish.tfusion.databinding.FragmentHomeBinding
import com.krish.tfusion.model.Student
import com.krish.tfusion.model.Teacher
import com.krish.tfusion.model.User
import com.krish.tfusion.utils.getTeacherMapped

private const val TAG = "HomeFragment"

class HomeFragment : Fragment(), CourseClick {
    private lateinit var binding: FragmentHomeBinding
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val mAdapter: TeacherAdapter by lazy { TeacherAdapter(this@HomeFragment) }
    private val args by navArgs<HomeFragmentArgs>()
    private val mDatabaseReference: DatabaseReference by lazy { FirebaseDatabase.getInstance().reference }
    private val mTutorList: ArrayList<Teacher> by lazy { ArrayList() }
    private lateinit var mTeacher: Teacher
    private lateinit var mStudent: Student
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragments
        setHasOptionsMenu(true)
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        Log.d(TAG, "onCreateView: ${args.user}")
        if (args.user?.isTeacher == true) {
            mTeacher = args.user?.user as Teacher
            binding.postFab.visibility = View.VISIBLE
        } else {
            mStudent = args.user?.user as Student
        }

        binding.postFab.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragment2ToPostVideoFragment(mTeacher)
            findNavController().navigate(action)
        }
        setUpRecyclerView()
        getTutorData()

        return binding.root
    }

    private fun getTutorData() {
        mDatabaseReference.child("users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    mTutorList.clear()
                    for (snap in snapshot.children) {
                        val tutors = snap.getValue(User::class.java)!!
                        if (tutors.isTeacher == true) {
                            tutors.user = getTeacherMapped(tutors.user as HashMap<*, *>)
                            mTutorList.add(tutors.user as Teacher)
                        }
                    }
                    mAdapter.setData(mTutorList)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    }

    private fun setUpRecyclerView() {
        binding.recyclerViewTeacher.adapter = mAdapter
        binding.recyclerViewTeacher.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun getCourse(currentTutor: Teacher) {
        val action = HomeFragmentDirections.actionHomeFragment2ToCourseFragment(currentTutor)
        findNavController().navigate(action)
    }


}


