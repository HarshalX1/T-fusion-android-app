package com.krish.tfusion.ui.signUpFragment

import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth

import com.krish.tfusion.databinding.FragmentSignUpBinding
import com.krish.tfusion.model.Student
import com.krish.tfusion.model.Teacher

import java.util.*
import java.util.regex.Pattern

private const val TAG = "SignUpFragment"

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding

    private val PASSWORD_PATTERN: Pattern = Pattern.compile(
        "^" +  //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +  //any letter
                "(?=.*[@#$%^&+=])" +  //at least 1 special character
                "(?=\\S+$)" +  //no white spaces
                ".{4,}" +  //at least 4 characters
                "$"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        val dataset: List<String> = LinkedList(listOf("Student", "Professor"))
        binding.userPosition.attachDataSource(dataset)


        binding.btnSignUp.setOnClickListener {

            val username = binding.etUsername.text.toString().trim { it <= ' ' }
            val email = binding.etEmail.text.toString().trim { it <= ' ' }
            val password = binding.etPassword.text.toString().trim { it <= ' ' }
            val userPosition = binding.userPosition.selectedItem.toString()

            if (validateUsername(username) && validateEmail(email) && validatePassword(password)) {
                if (userPosition == "Student"){
                    val student = Student(username, email)
                    val action = SignUpFragmentDirections.actionSignUpFragmentToStudentFragment(student,password)
                    findNavController().navigate(action)
                }else{
                    val tutor = Teacher(username, email)
                    val action = SignUpFragmentDirections.actionSignUpFragmentToTutorFragment(tutor,password)
                    findNavController().navigate(action)
                }
                clearField()
            }
        }

        return binding.root
    }

    private fun clearField() {
        binding.etUsername.text.clear()
        binding.etEmail.text.clear()
        binding.etPassword.text.clear()
    }

    // Validation for user input
    private fun validateEmail(email: String): Boolean {
        return if (email.isBlank()) {
            binding.etEmail.error = "Field can't be set empty"
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Please enter valid email"
            false
        } else {
            binding.etEmail.error = null
            true
        }
    }

    private fun validatePassword(password: String): Boolean {
        return if (password.isBlank()) {
            binding.etPassword.error = "Field can't be set empty"
            false
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            binding.etPassword.error =
                "a-z or A-Z letter\n1 Special Character\nNo White Spaces\nAt-least 4 Character"
            false
        } else {
            binding.etPassword.error = null
            true
        }
    }

    private fun validateUsername(username: String): Boolean {
        return if (username.isBlank()) {
            binding.etUsername.error = "Field can't be set empty"
            false
        } else {
            binding.etUsername.error = null
            true
        }
    }


}