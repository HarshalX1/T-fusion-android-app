package com.krish.tfusion.ui.friendFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.krish.tfusion.adapter.AllFriendAdapter
import com.krish.tfusion.databinding.FragmentAllBinding
import com.krish.tfusion.model.Student
import com.krish.tfusion.model.Teacher
import com.krish.tfusion.model.User
import com.krish.tfusion.utils.getStudentMapped
import com.krish.tfusion.utils.getTeacherMapped

private const val TAG = "AllFragment"
class AllFragment : Fragment() {

    private lateinit var binding: FragmentAllBinding
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val mFirebaseDatabase : FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }
    private val mFriendsList : ArrayList<String> by lazy { ArrayList() }
    private val mStudentList : ArrayList<Student> by lazy { ArrayList() }
    private val mTeacherList : ArrayList<Teacher> by lazy { ArrayList() }
    private lateinit var mAdapter : AllFriendAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAllBinding.inflate(inflater, container, false)

        val args = arguments
        val myBundle: User = args?.getParcelable<User>("currentUser") as User
        Log.d(TAG, "onCreateView: $myBundle")
        mAdapter = AllFriendAdapter(myBundle)
        setupRecyclerView()
        getAllFriends(myBundle)

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.allFriends.apply {
            setHasFixedSize(true)
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun getAllFriends(myBundle : User){
        val currentUserUid = mAuth.currentUser?.uid
        mFirebaseDatabase.reference.child("friends").child(currentUserUid!!)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (snap in snapshot.children)
                    {
                        mFriendsList.add(snap.key!!)
                    }
                    getFriendsProfile(myBundle)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "${error.message}")
                }

            })


    }

    private fun getFriendsProfile(myBundle: User) {
        mFirebaseDatabase.reference.child("users")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    mTeacherList.clear()
                    mStudentList.clear()
                    for(snap in snapshot.children)
                    {
                        if (mFriendsList.contains(snap.key))
                        {
                            val friends = snap.getValue(User::class.java)!!
                            if (friends.isTeacher == true)
                            {
                                friends.user =
                                    getTeacherMapped(friends.user as HashMap<*, *>)
                                mTeacherList.add(friends.user as Teacher)
                            }else{
                                friends.user =
                                    getStudentMapped(friends.user as HashMap<*, *>)
                                mStudentList.add(friends.user as Student)
                            }
                        }
                    }
                    if (myBundle.isTeacher == true) {
                        mAdapter.getStudent(mStudentList)
                    } else {
                        mAdapter.getTeacher(mTeacherList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "onCancelled: ${error.message}")
                }

            })

    }


}