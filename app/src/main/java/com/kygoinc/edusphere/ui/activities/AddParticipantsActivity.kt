package com.kygoinc.edusphere.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.kygoinc.edusphere.adapters.AddParticipantAdapter
import com.kygoinc.edusphere.adapters.OnParticipantListChangeListener
import com.kygoinc.edusphere.databinding.ActivityAddParticipantsBinding
import com.kygoinc.edusphere.models.User

class AddParticipantsActivity : AppCompatActivity(), OnParticipantListChangeListener {
    private lateinit var binding: ActivityAddParticipantsBinding
    private lateinit var addParticipantRecyclerView: RecyclerView
    private lateinit var groupId: String
    var userList = ArrayList<User>()

    var addPartisipantList = ArrayList<User>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddParticipantsBinding.inflate(layoutInflater)
        //        Initialize toolbar
        val toolbar = binding.myToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Add participants"

        addParticipantRecyclerView = binding.rcvAddParticipants
        addParticipantRecyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        getUserList()

        groupId = intent.getStringExtra("key").toString()
        val view = binding.root
        setContentView(view)

        window.statusBarColor = resources.getColor(R.color.blue_700)


        binding.floatingActionButtonAdd.setOnClickListener {
//            call function to add users to members under group node in firebase
            addParticipantsToGroup(groupId, addPartisipantList)
            Log.d("groupid", groupId)

        }


    }


    private fun addParticipantsToGroup(groupKey: String, participantsList: List<User>) {
        // Get a reference to the group's members node
        val groupMembersRef =
            FirebaseDatabase.getInstance().getReference("Groups/$groupKey/members")

        // Fetch the current members to check if a user already exists in the group
        groupMembersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var allParticipantsExist = true

                // Iterate through the participants list
                for (participant in participantsList) {
                    // Check if the user already exists in the group
                    if (!snapshot.hasChild(participant.uid)) {
                        // If any participant doesn't exist, set the flag to false
                        allParticipantsExist = false
                        break
                    }
                }

                if (allParticipantsExist) {
                    Toast.makeText(
                        this@AddParticipantsActivity,
                        "All participants already exist in the group",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val currentIndex = snapshot.childrenCount.toInt()

                    // Iterate through the participants list and add each user's uid with an incremented index
                    for ((index, participant) in participantsList.withIndex()) {
                        // Set the user's UID as a child node in the members node with an incremented index
                        groupMembersRef.child((currentIndex + index).toString()).setValue(participant.uid)

                    }

                    // Notify the user or take any necessary action
                    Toast.makeText(
                        this@AddParticipantsActivity,
                        "Participants added to the group",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error if fetching members fails
                Toast.makeText(
                    this@AddParticipantsActivity,
                    "Error adding participants to the group",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    private fun getUserList() {
        val firebase: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Users")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()

                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    val user = dataSnapshot.getValue(User::class.java)

                    if (!user!!.uid.equals(firebase.uid)) {
                        userList.add(user)
                    }
                }
                val userAdapter = AddParticipantAdapter(
                    this@AddParticipantsActivity,
                    userList,
                    addPartisipantList,
                    this@AddParticipantsActivity
                )
                Log.d("UserList", userList.toString())
                addParticipantRecyclerView.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@AddParticipantsActivity,
                    "Something went wrong: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Handle the back button press
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onParticipantListChanged(participantList: List<User>) {
        // Update your UI or perform any other action based on the participant list change
        if (participantList.size > 0) {
            binding.floatingActionButtonAdd.visibility = View.VISIBLE
        } else {
            binding.floatingActionButtonAdd.visibility = View.GONE
        }
    }
}