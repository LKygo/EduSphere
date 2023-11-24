package com.kygoinc.edusphere.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kygoinc.edusphere.R
import com.kygoinc.edusphere.models.GroupInfo
import com.kygoinc.edusphere.models.User
import com.kygoinc.edusphere.ui.activities.ConversationActivity
import com.kygoinc.edusphere.ui.activities.GroupChatActivity
import de.hdodenhof.circleimageview.CircleImageView

class CommunitiesAdapter(private val context: Context, private val communitiesList: ArrayList<GroupInfo>):
    RecyclerView.Adapter<CommunitiesAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_users, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val group = communitiesList[position]
        holder.userName.text = group.group
//        holder.userAbout.text = group.about
//        Glide.with(context).load(user.profile).placeholder(R.drawable.user_profile).into(holder.userProfile)
//        Glide.with(context).load(group.profile).into(holder.userProfile)

        holder.userLayout.setOnClickListener {
            val intent = Intent(context, GroupChatActivity::class.java)
            intent.putExtra("key", group.key)
            context.startActivity(intent)

        }
    }

    override fun getItemCount(): Int {
       return communitiesList.size
    }


    class ViewHolder(view: View): RecyclerView.ViewHolder(view){

        val userName: TextView = view.findViewById(R.id.txvChatUserName)
        val userProfile: CircleImageView = view.findViewById(R.id.imgChatUserProfile)
        val userAbout : TextView = view.findViewById(R.id.txvChatMessage)
//        val userMessage: TextView = view.findViewById(R.id.txvChatMessage)
        val userLayout : ConstraintLayout = view.findViewById(R.id.LayoutUser)

    }

}