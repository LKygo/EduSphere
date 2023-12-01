package com.kygoinc.edusphere.ui.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kygoinc.edusphere.R
import com.kygoinc.edusphere.adapters.UserAdapter
import com.kygoinc.edusphere.databinding.ActivityGroupInfoBinding
import com.kygoinc.edusphere.databinding.FragmentFriendsBinding
import com.kygoinc.edusphere.models.User

class GroupInfo : AppCompatActivity() {

    private var _binding: ActivityGroupInfoBinding? = null
    private lateinit var rcvGroupMembers: RecyclerView
    private val binding get() = _binding!!
    val groupMemberList = ArrayList<User>()
    val memberList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityGroupInfoBinding.inflate(layoutInflater)
        val view = binding.root

        window.statusBarColor = resources.getColor(R.color.blue_700)
        val groupId = intent.getStringExtra("key").toString()


        binding.backNav.setOnClickListener {
            finish()
        }

        fetchGroupMembers(groupId) { membersList ->

            binding.groupParticipants.text = buildString {
                append("Group · ")
                append(membersList.size.toString())
                append(" participants")
            }
            Log.d("members", membersList.toString())
            fetchGroupMembersInfo(membersList, groupMemberList) {
                Log.d("members", groupMemberList.toString())
                rcvGroupMembers = binding.rcvGroupMembers
                rcvGroupMembers.layoutManager =
                    LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                val groupMembersAdapter = UserAdapter(this, groupMemberList)
                rcvGroupMembers.adapter = groupMembersAdapter
            }
        }
        setContentView(view)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    private fun fetchGroupMembers(groupId: String, callback: (List<String>) -> Unit) {
        val databaseReference =
            FirebaseDatabase.getInstance().getReference("Groups/$groupId/members")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val uniqueUserIds = HashSet<String>()

                for (childSnapshot in snapshot.children) {
                    val userId = childSnapshot.value as String
                    uniqueUserIds.add(userId)
                }

                val uniqueMemberList = uniqueUserIds.toList()
                callback.invoke(uniqueMemberList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
                callback.invoke(emptyList())
            }
        })
    }

    // Function to fetch group members' info
    fun fetchGroupMembersInfo(
        memberList: List<String>,
        groupMemberList: ArrayList<User>,
        callback: () -> Unit
    ) {
        // Reset the existing list
        groupMemberList.clear()

        // Counter to keep track of how many users' info have been fetched
        var fetchedCount = 0

        for (userId in memberList) {
            fetchUserInfo(userId) { userInfo ->
                // Increment the counter
                fetchedCount++

                // Check if the user info is not null and not already in the list
                if (userInfo != null && !groupMemberList.contains(userInfo)) {
                    groupMemberList.add(userInfo)
                }

                // Check if all users' info have been fetched
                if (fetchedCount == memberList.size) {
                    // Notify the callback when all info is fetched
                    callback()
                }
            }
        }
    }


    // Function to fetch user information by user ID
    private fun fetchUserInfo(userId: String, callback: (User?) -> Unit) {
        val userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                callback(user)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }


}