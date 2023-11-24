package com.kygoinc.edusphere.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ValueEventListener
import com.kygoinc.edusphere.R
import com.kygoinc.edusphere.models.Chat
import com.kygoinc.edusphere.models.GroupChat
import de.hdodenhof.circleimageview.CircleImageView

class GroupChatAdapter(
    private val context: Context,
    private val groupChatList: ArrayList<GroupChat>
) :
    RecyclerView.Adapter<GroupChatAdapter.ViewHolder>() {

    private val MESSAGE_TYPE_LEFT = 0
    private val MESSAGE_TYPE_RIGHT = 1
    var firebaseuser: FirebaseUser? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == MESSAGE_TYPE_RIGHT) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_group_right, parent, false)
            ViewHolder(view)


        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_group_left, parent, false)
            ViewHolder(view)

        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = groupChatList[position]
        holder.userName.text = chat?.senderName
        holder.userText.text = chat.message
//        Glide.with(context).load(chat.profile).into(holder.userProfile)

    }

    override fun getItemCount(): Int {
        return groupChatList.size
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val userText: TextView = view.findViewById(R.id.txvGMessage)
        val userName: TextView = view.findViewById(R.id.txvGUsername)
//        val userProfile: CircleImageView = view.findViewById(R.id.imgProfile)
//        val userMessage: TextView = view.findViewById(R.id.txvChatMessage)

    }

    override fun getItemViewType(position: Int): Int {
        firebaseuser = FirebaseAuth.getInstance().currentUser
        val currentUserId = firebaseuser?.uid ?: ""
        val senderId = groupChatList[position].senderId
        Log.d("GroupChatAdapter", "CurrentUserId: $currentUserId, SenderId: $senderId")

        return if (senderId == currentUserId) {
            MESSAGE_TYPE_RIGHT
        } else {
            MESSAGE_TYPE_LEFT
        }
    }

}