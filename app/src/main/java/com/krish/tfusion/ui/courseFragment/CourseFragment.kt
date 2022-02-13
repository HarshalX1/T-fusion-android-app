package com.krish.tfusion.ui.courseFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.krish.tfusion.adapter.VideoAdapter
import com.krish.tfusion.databinding.FragmentCourseBinding
import com.krish.tfusion.model.Video

private const val TAG = "CourseFragment"
class CourseFragment : Fragment() {
    private lateinit var binding: FragmentCourseBinding
    private val args by navArgs<CourseFragmentArgs>()
    private val mAdapter: VideoAdapter by lazy { VideoAdapter() }
    private val mFirebaseDatabase : FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }
    private val mVideoList : ArrayList<Video> by lazy { ArrayList() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCourseBinding.inflate(inflater, container, false)

        setupRecyclerView()
        getVideoFromDb()

        return binding.root
    }

    private fun getVideoFromDb() {
        mFirebaseDatabase.reference.child("videos").child(args.currentTutor?.uid!!)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    mVideoList.clear()
                    for(snap in snapshot.children){
                        val video = snap.getValue(Video::class.java)!!
                        mVideoList.add(video)
                    }
                    mAdapter.setData(mVideoList)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

    }

    private fun setupRecyclerView() {
        binding.videoRecyclerView.adapter = mAdapter
        binding.videoRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}