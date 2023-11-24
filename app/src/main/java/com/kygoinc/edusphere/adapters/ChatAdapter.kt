package com.kygoinc.edusphere.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kygoinc.edusphere.R
import com.kygoinc.edusphere.models.Chat
import com.kygoinc.edusphere.models.GroupChat

class ChatAdapter(private val context: Context, private val chatList: ArrayList<Chat>) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private val MESSAGE_TYPE_LEFT = 0
    private val MESSAGE_TYPE_RIGHT = 1
    var firebaseuser: FirebaseUser? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == MESSAGE_TYPE_RIGHT) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_right, parent, false)
            ViewHolder(view)


        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_left, parent, false)
            ViewHolder(view)

        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chatList[position]
        holder.userName.text = chat.message
//        Glide.with(context).load(chat.profile).into(holder.userProfile)

    }

    override fun getItemCount(): Int {
        return chatList.size
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val userName: TextView = view.findViewById(R.id.txvMessage)
//        val userProfile: CircleImageView = view.findViewById(R.id.imgProfile)
//        val userMessage: TextView = view.findViewById(R.id.txvChatMessage)

    }

    override fun getItemViewType(position: Int): Int {
        firebaseuser = FirebaseAuth.getInstance().currentUser
        return if (chatList[position].senderId == firebaseuser!!.uid) {
            MESSAGE_TYPE_RIGHT
        } else {
            MESSAGE_TYPE_LEFT
        }
    }

}