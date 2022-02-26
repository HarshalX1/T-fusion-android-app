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
import com.krish.tfusion.adapter.AddBtnClick
import com.krish.tfusion.adapter.FriendsAdapter
import com.krish.tfusion.databinding.FragmentAddBinding
import com.krish.tfusion.model.FriendRequest
import com.krish.tfusion.model.Student
import com.krish.tfusion.model.Teacher
import com.krish.tfusion.model.User
import com.krish.tfusion.utils.getStudentMapped
import com.krish.tfusion.utils.getTeacherMapped

private const val TAG = "AddFragment"

class AddFragment : Fragment(), AddBtnClick {
    private lateinit var binding: FragmentAddBinding
    private val mTeacherList: ArrayList<Teacher> by lazy { ArrayList() }
    private val mStudentList: ArrayList<Student> by lazy { ArrayList() }
    private val mFirebaseDatabase: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }
    private val mFirebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private lateinit var mFriendAdapter: FriendsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddBinding.inflate(inflater, container, false)

        val args = arguments
        val myBundle: User = args?.getParcelable<User>("currentUser") as User
        Log.d(TAG, "onCreateView: $myBundle")
        mFriendAdapter = FriendsAdapter(myBundle, this)
        setUpRecyclerView()
        getUsers(myBundle)



        return binding.root
    }

    private fun setUpRecyclerView() {
        binding.recyclerView.adapter = mFriendAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun getUsers(myBundle: User) {
        mFirebaseDatabase.reference.child("users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    mTeacherList.clear()
                    mStudentList.clear()
                    for (snap in snapshot.children) {
                        val allUser = snap.getValue(User::class.java)!!
                        if (allUser.isTeacher == true) {
                            allUser.user = getTeacherMapped(allUser.user as HashMap<*, *>)
                            mTeacherList.add(allUser.user as Teacher)
                        } else {
                            allUser.user = getStudentMapped(allUser.user as HashMap<*, *>)
                            mStudentList.add(allUser.user as Student)
                        }
                    }

                    if (myBundle.isTeacher == true) {
                        mFriendAdapter.setStudentData(mStudentList)
                    } else {
                        mFriendAdapter.setTeacherData(mTeacherList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "onCancelled: $error")
                }

            })
    }

    override fun sendFriendRequest(friendRequest: FriendRequest) {
        val creationTimeMs = System.currentTimeMillis().toString()
        friendRequest.creationTimeMs = creationTimeMs
        val ref =
            mFirebaseDatabase.reference.child("requests").child(friendRequest.receiverUID!!).child(friendRequest.senderUID!!)
        ref.setValue(friendRequest.senderUID)
            .addOnSuccessListener {
                Log.d(TAG, "sendFriendRequest: working fine")
            }
            .addOnFailureListener {
                Log.d(TAG, "sendFriendRequest: not working fine")
            }
    }

}