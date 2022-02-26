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
import com.krish.tfusion.adapter.AcceptDeniedRequest
import com.krish.tfusion.adapter.PendingAdapter
import com.krish.tfusion.databinding.FragmentPendingBinding
import com.krish.tfusion.model.Student
import com.krish.tfusion.model.Teacher
import com.krish.tfusion.model.User
import com.krish.tfusion.utils.getStudentMapped
import com.krish.tfusion.utils.getTeacherMapped

private const val TAG = "PendingFragment"

class PendingFragment : Fragment(), AcceptDeniedRequest {

    private lateinit var binding: FragmentPendingBinding
    private val mFirebaseDatabase: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val mFriendRequest: ArrayList<String> by lazy { ArrayList() }
    private val mStudentList: ArrayList<Student> by lazy { ArrayList() }
    private val mTeacherList: ArrayList<Teacher> by lazy { ArrayList() }
    private lateinit var mPendingAdapter: PendingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPendingBinding.inflate(inflater, container, false)

        val args = arguments
        val myBundle: User = args?.getParcelable<User>("currentUser") as User
        Log.d(TAG, "onCreateView: $myBundle")
        mPendingAdapter = PendingAdapter(myBundle, this)
        getFriendRequest(myBundle)
        setUpRecyclerView()

        return binding.root
    }

    private fun setUpRecyclerView() {
        binding.pendingRecyclerView.apply {
            adapter = mPendingAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun getFriendRequest(myBundle: User) {
        val currentUID = mAuth.currentUser?.uid
        mFirebaseDatabase.reference.child("requests").child(currentUID!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (snap in snapshot.children) {
                        mFriendRequest.add(snap.key!!)
                    }
                    getProfileDetails(myBundle, mFriendRequest)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun getProfileDetails(myBundle: User, mFriendRequest: ArrayList<String>) {
        mFirebaseDatabase.reference.child("users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    mTeacherList.clear()
                    mStudentList.clear()
                    for (snap in snapshot.children) {
                        if (mFriendRequest.contains(snap.key)) {
                            Log.d(TAG, "onDataChange: We are inside this")
                            val pendingUser = snap.getValue(User::class.java)!!
                            if (pendingUser.isTeacher == true) {
                                pendingUser.user =
                                    getTeacherMapped(pendingUser.user as HashMap<*, *>)
                                mTeacherList.add(pendingUser.user as Teacher)
                            } else {
                                pendingUser.user =
                                    getStudentMapped(pendingUser.user as HashMap<*, *>)
                                mStudentList.add(pendingUser.user as Student)
                            }
                        }
                    }
                    if (myBundle.isTeacher == true) {
                        mPendingAdapter.getStudent(mStudentList)
                    } else {
                        mPendingAdapter.getTeacher(mTeacherList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "onCancelled: ${error.message}")
                }

            })
    }

    override fun acceptRequest(uid: String) {
        val currentUid = mAuth.currentUser?.uid
        mFirebaseDatabase.reference.child("requests").child(currentUid!!).child(uid).removeValue()
            .addOnSuccessListener {
                Log.d(TAG, "acceptRequest: Keep Working")
            }
            .addOnFailureListener {
                Log.d(TAG, "acceptRequest: Not Working")
            }
        addFriendsToDatabase(currentUid, uid)
    }

    private fun addFriendsToDatabase(currentUid: String, uid: String) {
        mFirebaseDatabase.reference.child("friends").child(currentUid).child(uid)
            .setValue(uid)
            .addOnSuccessListener {
                Log.d(TAG, "addFriendsToDatabase: $it")
            }
            .addOnFailureListener {
                Log.d(TAG, "addFriendsToDatabase: ${it.message}")
            }

        mFirebaseDatabase.reference.child("friends").child(uid).child(currentUid)
            .setValue(currentUid)
            .addOnSuccessListener {
                Log.d(TAG, "addFriendsToDatabase: $it")
            }
            .addOnFailureListener {
                Log.d(TAG, "addFriendsToDatabase: ${it.message}")
            }
    }

    override fun deniedRequest(uid: String) {
        val currentUid = mAuth.currentUser?.uid
        mFirebaseDatabase.reference.child("requests").child(currentUid!!).child(uid).removeValue()
            .addOnSuccessListener {
                Log.d(TAG, "acceptRequest: Keep Working")
            }
            .addOnFailureListener {
                Log.d(TAG, "acceptRequest: Not Working")
            }
    }

}