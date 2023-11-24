package com.kygoinc.edusphere.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kygoinc.edusphere.databinding.FragmentProfileBinding
import com.kygoinc.edusphere.models.User

class ProfileFragment : Fragment() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var refUsers: DatabaseReference
    private lateinit var firebaseUserId: String
    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        fetchAndDisplayUserData()

        binding.btnUpdate.setOnClickListener {
            updateUserInfo()
        }
    }

    private fun updateUserInfo() {
        // Retrieve updated information from input fields
        val newUsername = binding.edtUserName.text.toString()
        val newAbout = binding.edtAbout.text.toString()
        val newPhone = binding.edtPhone.text.toString()

        // Update the user's information in the database
        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Users")

        firebaseUser?.uid?.let {
            val userRef = databaseReference.child(it)

            // Update only the fields that are not empty
            if (newUsername.isNotEmpty()) {
                userRef.child("username").setValue(newUsername)
            }
            if (newAbout.isNotEmpty()) {
                userRef.child("about").setValue(newAbout)
            }
            if (newPhone.isNotEmpty()) {
                userRef.child("phone").setValue(newPhone)
            }
            Toast.makeText(
                requireContext(),
                "Profile updated successfully",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun updateUIWithUserData(it: User) {
        binding.edtUserName.setText(it.username)
        binding.edtAbout.setText(it.about)
//        binding.tvStatus.text = it.status

    }

    private fun fetchAndDisplayUserData() {
        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Users")

        firebaseUser?.uid?.let {
            val userRef = databaseReference.child(it)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(User::class.java)

                        // Call a method to update the UI with user data
                        user?.let { updateUIWithUserData(it) }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        requireContext(),
                        "Something went wrong: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}