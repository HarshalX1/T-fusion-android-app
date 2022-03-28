package com.krish.tfusion.ui.loginFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.krish.tfusion.R
import com.krish.tfusion.databinding.FragmentLoginBinding
import com.krish.tfusion.model.Student
import com.krish.tfusion.model.Teacher
import com.krish.tfusion.model.User
import com.krish.tfusion.utils.getStudentMapped
import com.krish.tfusion.utils.getTeacherMapped
import java.util.*

private const val TAG = "LoginFragment"

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val mDatabase: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)


        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            authenticate(email, password)

        }

        return binding.root
    }

    private fun authenticate(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = mAuth.currentUser?.uid
                getUserFromDatabase(uid)
            }
    }

    private fun getUserFromDatabase(uid: String?) {
        val ref = mDatabase.getReference("users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentUser = snapshot.getValue(User::class.java)!!
                val user = currentUser.user as HashMap<*, *>
                if (currentUser.isTeacher == true) {
                    currentUser.user = getTeacherMapped(user)
                } else {
                    currentUser.user = getStudentMapped(user)
                    Log.d(TAG, "onDataChange: ${currentUser.user}")
                }
                val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment2(currentUser)
                findNavController().navigate(action)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }




}