package com.kygoinc.edusphere.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kygoinc.edusphere.R
import com.kygoinc.edusphere.adapters.Communities
import com.kygoinc.edusphere.adapters.CommunitiesAdapter
import com.kygoinc.edusphere.adapters.UserAdapter
import com.kygoinc.edusphere.databinding.FragmentCommunitiesBinding
import com.kygoinc.edusphere.databinding.FragmentFriendsBinding
import com.kygoinc.edusphere.models.GroupInfo
import com.kygoinc.edusphere.models.User


class CommunitiesFragment : Fragment() {

    private var _binding: FragmentCommunitiesBinding? = null
    private lateinit var communitiesRecyclerView: RecyclerView
    private val binding get() = _binding!!
    var communitiesList = ArrayList<GroupInfo>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCommunitiesBinding.inflate(inflater, container, false)
        val view = binding.root

        communitiesRecyclerView = binding.rcvCommunitiesRecyclerView
        communitiesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        getCommunitiesList()


        return view
    }

    private fun getCommunitiesList() {
// Get all groups from firebase database
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Groups")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                communitiesList.clear()

                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    val group = dataSnapshot.getValue(GroupInfo::class.java)

                    // Check if the user is a member of the group
                    if (group?.members?.contains(userId) == true) {
                        communitiesList.add(group)
                    }

                }
                val communitiesAdapter = CommunitiesAdapter(requireContext(), communitiesList)
                Log.d("Communities", communitiesList.toString())
                communitiesRecyclerView.adapter = communitiesAdapter

            }

            override fun onCancelled(error: DatabaseError) {
               Log.d("Communities", "Error getting communities: $error")
            }


        })
    }
}