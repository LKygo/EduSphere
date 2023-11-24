package com.kygoinc.edusphere.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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
import com.kygoinc.edusphere.adapters.ChatAdapter
import com.kygoinc.edusphere.adapters.GroupChatAdapter
import com.kygoinc.edusphere.databinding.ActivityGroupChatBinding
import com.kygoinc.edusphere.models.GroupChat
import com.kygoinc.edusphere.models.GroupI
import com.kygoinc.edusphere.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GroupChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGroupChatBinding
    private lateinit var gChatRecycler: RecyclerView
    private lateinit var userId: String
    private lateinit var userName: String
    private var groupId: String = ""
    var firebaseUser: FirebaseUser? = null
    var reference: DatabaseReference? = null
    var groupChatList = ArrayList<GroupChat>()
    val databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("Users")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupChatBinding.inflate(layoutInflater)

//        Initialize toolbar
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(null)

        val view = binding.root
        setContentView(view)

        gChatRecycler = binding.rcvGChatRecycler

        gChatRecycler.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        var intent = intent
        groupId = intent.getStringExtra("key").toString()


        firebaseUser = FirebaseAuth.getInstance().currentUser!!


        reference = FirebaseDatabase.getInstance().getReference("Groups").child(groupId!!)
        val userRef = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)

        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val groupChat = snapshot.getValue(GroupI::class.java)
                binding.convGroupName.text = groupChat!!.group
//                Glide.with(this@GroupChatActivity).load(groupChat!!.profile)
//                    .into(binding.imgProfilePic)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO()
            }
        })





        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Assuming User class has a constructor that accepts ID and username
                val user = snapshot.getValue(User::class.java)

                if (user != null) {
                    userName = user.username
                    userId = user.uid
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        binding.edtComposeMessage.setOnClickListener {
            GlobalScope.launch {
                delay(2000)
                withContext(Dispatchers.Main) {
                    val gChatAdapter = GroupChatAdapter(this@GroupChatActivity, groupChatList)
                    gChatRecycler.scrollToPosition(gChatAdapter.itemCount - 1)
                }

            }
        }

        binding.btnSend.setOnClickListener {
            var message = binding.edtComposeMessage.text.toString()

            if (message.isEmpty()) {
                Toast.makeText(this, "Can't send a blank message", Toast.LENGTH_SHORT).show()
                binding.edtComposeMessage.setText("")

            } else {
                sendGroupMessage(userId, userName, message, groupId)
                binding.edtComposeMessage.setText("")
            }
        }
        readGroupMessage(groupId)


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(this).inflate(R.menu.group_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Handle the back button press
                finish()
                return true
            }
            R.id.addMember -> {
//               Navigate to add member activity
                val intent = Intent(this, AddParticipantsActivity::class.java)
                intent.putExtra("key", groupId)
                startActivity(intent)
            }
            R.id.deleteGroup -> {
                // Handle the delete group option
                Toast.makeText(this, "You have deleted the group", Toast.LENGTH_SHORT).show()
                finish()
            }
            R.id.groupSettings -> {
                // Handle the edit group option
                Toast.makeText(this, "You have edited the group", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sendGroupMessage(
        senderId: String,
        senderName: String,
        message: String,
        groupId: String
    ) {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().reference
        val groupChat = GroupChat(
            groupId = groupId,
            messageId = reference.child("GroupChat").push().key!!,
            senderId = senderId,
            senderName = senderName,
            message = message,
            timestamp = System.currentTimeMillis()
        )
        reference.child("GroupChat").child(groupChat.messageId).setValue(groupChat)
    }

    private fun readGroupMessage(groupId: String) {
        val reference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("GroupChat")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                groupChatList.clear()
                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    val chat = dataSnapshot.getValue(GroupChat::class.java)

                    // Add your filtering logic based on group ID
                    if (chat != null && chat.groupId == groupId) {
                        groupChatList.add(chat)
                    }
                }

                val chatAdapter = GroupChatAdapter(this@GroupChatActivity, groupChatList)
                Log.d("gChat", groupChatList.toString())
                gChatRecycler.adapter = chatAdapter
                gChatRecycler.scrollToPosition(chatAdapter.itemCount - 1)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled
            }
        })
    }
}