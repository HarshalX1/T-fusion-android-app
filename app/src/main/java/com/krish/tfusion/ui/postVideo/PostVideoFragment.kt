package com.krish.tfusion.ui.postVideo

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.firebase.database.FirebaseDatabase
import com.krish.tfusion.databinding.FragmentPostVideoBinding
import com.krish.tfusion.model.Video

private const val TAG = "PostVideoFragment"

class PostVideoFragment : Fragment() {

    private lateinit var binding: FragmentPostVideoBinding
    private val args by navArgs<PostVideoFragmentArgs>()
    private val mFirebaseDatabase: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPostVideoBinding.inflate(inflater, container, false)

        binding.btnUpload.setOnClickListener {
            val title = binding.etTitle.text.toString().trim { it <= ' ' }
            val description = binding.etVideoDescription.text.toString().trim { it <= ' ' }
            var videoLink = binding.etVideoLink.text.toString().trim { it <= ' ' }
            val currentTutor = args.currentTutor!!
            if (validateTitle(title) && validateDescription(description) && validateVideoLink(
                    videoLink
                )
            ) {
                videoLink = formatLink(videoLink)
                val video = Video(
                    currentTutor.username,
                    currentTutor.imageUrl,
                    currentTutor.uid,
                    title,
                    description,
                    videoLink
                )
                uploadVideoToDatabase(video)
                clearFields()
            }
        }
        return binding.root

    }

    private fun clearFields() {
        binding.etTitle.text.clear()
        binding.etVideoDescription.text.clear()
        binding.etVideoLink.text.clear()
    }

    private fun uploadVideoToDatabase(video: Video) {
        val ref = mFirebaseDatabase.reference.child("videos").child("${video.uid}").push()
        ref.setValue(video)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Video Upload Success", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Video Upload Failure", Toast.LENGTH_SHORT).show()
            }
    }

    private fun formatLink(videoLink: String): String {
        val baseUrl = "https://www.youtube.com/embed/"
        val videoUid = videoLink.split('=')[1]
        return baseUrl + videoUid
    }

    private fun validateVideoLink(videoLink: String): Boolean {
        return if (videoLink.isBlank()) {
            binding.etVideoLink.error = "Field can't be set empty"
            false
        } else if (!Patterns.WEB_URL.matcher(videoLink).matches()) {
            binding.etVideoLink.error = "Please enter valid url"
            false
        } else {
            true
        }
    }

    private fun validateDescription(description: String): Boolean {
        return if (description.isBlank()) {
            binding.etVideoDescription.error = "Field can't be set empty"
            false
        } else {
            true
        }
    }

    private fun validateTitle(title: String): Boolean {
        return if (title.isBlank()) {
            binding.etTitle.error = "Field can't be set empty"
            false
        } else {
            true
        }
    }
}