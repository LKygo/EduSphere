package com.kygoinc.edusphere.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kygoinc.edusphere.adapters.ChatAdapter
import com.kygoinc.edusphere.databinding.ActivityConversationBinding
import com.kygoinc.edusphere.models.Chat
import com.kygoinc.edusphere.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConversationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConversationBinding
    private lateinit var chatRecycler: RecyclerView
    var firebaseUser: FirebaseUser? = null
    var reference: DatabaseReference? = null
    var chatList = ArrayList<Chat>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConversationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        chatRecycler = binding.rcvChatRecycler

        chatRecycler.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        var intent = intent
        var userId = intent.getStringExtra("uid")

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId!!)

        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                binding.convUserName.text = user!!.username
                Glide.with(this@ConversationActivity).load(user!!.profile)
                    .into(binding.imgProfilePic)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        binding.edtComposeMessage.setOnClickListener {
            GlobalScope.launch {
                delay(2000)
                withContext(Dispatchers.Main) {
                    val chatAdapter = ChatAdapter(this@ConversationActivity, chatList)
                    chatRecycler.scrollToPosition(chatAdapter.itemCount - 1)
                }

            }
        }

        binding.btnSend.setOnClickListener {
            var message = binding.edtComposeMessage.text.toString()

            if (message.isEmpty()) {
                Toast.makeText(this, "Can't send a blank message", Toast.LENGTH_SHORT).show()
                binding.edtComposeMessage.setText("")

            } else {
                sendMessage(firebaseUser!!.uid, userId, message)
                binding.edtComposeMessage.setText("")
            }
        }

        readMessage(firebaseUser!!.uid, userId)


    }

    private fun sendMessage(senderId: String, receiverId: String, message: String) {
        var reference: DatabaseReference = FirebaseDatabase.getInstance().reference
        var hashMap: HashMap<String, String> = HashMap()
        hashMap.put("senderId", senderId)
        hashMap.put("receiverId", receiverId)
        hashMap.put("message", message)

        reference.child("Chat").push().setValue(hashMap)
    }

    private fun readMessage(senderId: String, receiverId: String) {
        var reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("chat")
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Chat")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    val chat = dataSnapshot.getValue(Chat::class.java)

                    if (chat!!.senderId.equals(senderId) && chat.receiverId.equals(receiverId) ||
                        chat.receiverId.equals(senderId) && chat.senderId.equals(receiverId)
                    ) {
                        chatList.add(chat)
                    }
                }
                val chatAdapter = ChatAdapter(this@ConversationActivity, chatList)
                Log.d("chat", chatList.toString())
                chatRecycler.adapter = chatAdapter
                chatRecycler.scrollToPosition(chatAdapter.itemCount - 1)


            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("chat", error.message)
            }
        })
    }
}