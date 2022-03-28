package com.krish.tfusion.ui.tutorInfoFragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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
import com.krish.tfusion.databinding.FragmentTutorBinding
import com.krish.tfusion.model.Teacher
import com.krish.tfusion.model.User
import com.krish.tfusion.R
import java.util.*

private const val TAG = "TutorFragment"

class TutorFragment : Fragment() {

    private lateinit var binding: FragmentTutorBinding
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val mFirebaseStorage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }
    private val mFirebaseDatabase: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }
    private val args by navArgs<TutorFragmentArgs>()
    var selectedPhotoUri: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTutorBinding.inflate(inflater, container, false)
        val dataset1: List<String> = LinkedList(listOf("JEE", "NEET", "HSC"))
        binding.courseExpert.attachDataSource(dataset1)
        val dataset2: List<String> = LinkedList(listOf("Physics", "Chemistry", "Math", "Biology"))
        binding.subjectExpert.attachDataSource(dataset2)

        binding.btnPhotoPick.setOnClickListener {
            getPhoto()
        }
        binding.circularImage.setOnClickListener {
            getPhoto()
        }

        binding.btnSubmit.setOnClickListener {
            val username = args.currentTutor?.username
            val email = args.currentTutor?.email
            val password = args.tutorPassword
            val collegeName = binding.etSchoolName.text.toString().trim { it <= ' ' }.lowercase()
            val subjectExpert = binding.subjectExpert.selectedItem.toString()
            val courseExpert = binding.courseExpert.selectedItem.toString()
            if (validateCollegeName(collegeName)) {
                if (selectedPhotoUri == null) {
                    Toast.makeText(requireContext(), "Please select Photo", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val tutor = Teacher(
                        username,
                        email,
                        collegeName = collegeName,
                        subjectExpert = subjectExpert,
                        courseExpert = courseExpert,
                        teacher = true
                    )
                    authenticateTutor(tutor, password)
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

    private fun authenticateTutor(tutor: Teacher, password: String?) {
        mAuth.createUserWithEmailAndPassword(tutor.email!!, password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = mAuth.currentUser?.uid!!
                    uploadImageToDatabase(tutor, uid)
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

    private fun uploadImageToDatabase(tutor: Teacher, uid: String) {
        val fileName = UUID.randomUUID().toString()
        val ref = mFirebaseStorage.getReference("/images/$fileName")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    saveUserToDatabase(tutor, uri, uid)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error : $exception", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserToDatabase(tutor: Teacher, uri: Uri?, uid: String) {
        val ref = mFirebaseDatabase.getReference("users/$uid")
        tutor.imageUrl = uri.toString()
        tutor.uid = uid
        val user = User(true, tutor)
        ref.setValue(user)
            .addOnSuccessListener {
                findNavController().navigate(R.id.action_tutorFragment_to_loginFragment)
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