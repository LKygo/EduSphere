package com.kygoinc.edusphere.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kygoinc.edusphere.R
import com.kygoinc.edusphere.models.User
import com.kygoinc.edusphere.ui.activities.ConversationActivity
import de.hdodenhof.circleimageview.CircleImageView

interface OnParticipantListChangeListener {
    fun onParticipantListChanged(participantList: List<User>)
}

class AddParticipantAdapter(
    private val context: Context,
    private val userList: ArrayList<User>,
    private val addParticipantList: ArrayList<User>,
    private val listener: OnParticipantListChangeListener
) :
    RecyclerView.Adapter<AddParticipantAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_users, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        holder.userName.text = user.username
        holder.userAbout.text = user.about
//        Glide.with(context).load(user.profile).placeholder(R.drawable.user_profile).into(holder.userProfile)
        Glide.with(context).load(user.profile).into(holder.userProfile)


        // Set background color based on whether the user is selected
        if (addParticipantList.contains(user)) {
            holder.userLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.blue_700))
        } else {
            holder.userLayout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        }

        holder.userLayout.setOnClickListener {
            // Toggle user selection on click
            if (addParticipantList.contains(user)) {
                addParticipantList.remove(user)
            } else {
                addParticipantList.add(user)
            }

            // Notify the listener
            // Notify the listener about the change
            listener.onParticipantListChanged(addParticipantList)
            Log.d("participant", addParticipantList.toString())


        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }




    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val userName: TextView = view.findViewById(R.id.txvChatUserName)
        val userProfile: CircleImageView = view.findViewById(R.id.imgChatUserProfile)
        val userAbout: TextView = view.findViewById(R.id.txvChatMessage)

        //        val userMessage: TextView = view.findViewById(R.id.txvChatMessage)
        val userLayout: ConstraintLayout = view.findViewById(R.id.LayoutUser)

    }



}