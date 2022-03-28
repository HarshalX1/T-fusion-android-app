package com.krish.tfusion.ui.studentInfoFragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.krish.tfusion.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.krish.tfusion.databinding.FragmentStudentBinding
import com.krish.tfusion.model.Student
import com.krish.tfusion.model.User
import java.util.*

private const val TAG = "StudentFragment"

class StudentFragment : Fragment() {
    private lateinit var binding: FragmentStudentBinding
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val mFirebaseStorage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }
    private val mFirebaseDatabase: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }
    private val args by navArgs<StudentFragmentArgs>()
    var selectedPhotoUri: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStudentBinding.inflate(inflater, container, false)

        val dataset: List<String> = LinkedList(listOf("JEE", "NEET", "HSC"))
        binding.courseEnrolled.attachDataSource(dataset)

        binding.btnPhotoPick.setOnClickListener {
            getPhoto()
        }
        binding.circularImage.setOnClickListener {
            getPhoto()
        }

        binding.btnSubmit.setOnClickListener {
            val username = args.currentStudent?.username
            val email = args.currentStudent?.email
            val password = args.studentPassword
            val collegeName = binding.etSchoolName.text.toString().trim { it <= ' ' }.lowercase()
            val courseEnrolled = binding.courseEnrolled.selectedItem.toString()
            if (validateCollegeName(collegeName)) {
                if (selectedPhotoUri == null) {
                    Toast.makeText(requireContext(), "Please select Photo", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val student = Student(
                        username,
                        email,
                        collegeName = collegeName,
                        courseEnrolled = courseEnrolled,
                        student = true
                    )
                    authenticateStudent(student, password)
                    clearField()
                }
            }
        }


        return binding.root
    }

    private fun clearField() {
        binding.etSchoolName.text.clear()
        binding.circularImage.setImageBitmap(null)
        binding.btnPhotoPick.visibility = View.VISIBLE
    }

    private fun authenticateStudent(student: Student, password: String?) {
        mAuth.createUserWithEmailAndPassword(student.email!!, password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = mAuth.currentUser?.uid!!
                    uploadImageToDatabase(student, uid)
                }
            }
            .addOnFailureListener { exception ->
                try {
                    throw exception
                } catch (e: FirebaseAuthWeakPasswordException) {
                    Toast.makeText(requireContext(), "Error : Weak Password", Toast.LENGTH_SHORT)
                        .show()
                } catch (e: FirebaseAuthEmailException) {
                    Toast.makeText(requireContext(), "Error : Email Not Valid", Toast.LENGTH_SHORT)
                        .show()
                } catch (e: FirebaseAuthUserCollisionException) {
                    Toast.makeText(
                        requireContext(),
                        "Error : Email id already taken",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } catch (e: FirebaseAuthInvalidUserException) {
                    Toast.makeText(requireContext(), "Error : Invalid User", Toast.LENGTH_SHORT)
                        .show()
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(
                        requireContext(),
                        "Error : Credentials Invalid",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error : $exception", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun uploadImageToDatabase(student: Student, uid: String) {
        val fileName = UUID.randomUUID().toString()
        val ref = mFirebaseStorage.getReference("/images/$fileName")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    saveUserToDatabase(student, uri, uid)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error : $exception", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserToDatabase(student: Student, uri: Uri?, uid: String) {
        val ref = mFirebaseDatabase.getReference("users/$uid")
        student.imageUrl = uri.toString()
        student.uid = uid
        val user = User(false, student)
        ref.setValue(user)
            .addOnSuccessListener {
                findNavController().navigate(R.id.action_studentFragment_to_loginFragment)
            }
    }

    private fun validateCollegeName(collegeName: String): Boolean {
        return if (collegeName.isBlank()) {
            binding.etSchoolName.error = "Field can't be set empty"
            false
        } else {
            true
        }
    }

    // Taking Photo From Gallery
    private fun getPhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(
                requireContext().contentResolver,
                selectedPhotoUri
            )
            binding.btnPhotoPick.visibility = View.INVISIBLE
            binding.circularImage.setImageBitmap(bitmap)
        }
    }


}