package com.kygoinc.edusphere.adapters

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kygoinc.edusphere.R
import com.kygoinc.edusphere.models.ImageResource

// ImageResourceAdapter.kt
class ImageResourceAdapter(
    private val context: Context,
    private val imageList: ArrayList<ImageResource>,
    var firebaseUser: FirebaseUser? = null
) :
    RecyclerView.Adapter<ImageResourceAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imgResourceImage)
        val btnDownload: ImageView = view.findViewById(R.id.imgDownloadImage)
        val title: TextView = view.findViewById(R.id.txvTitle)
        val description: TextView = view.findViewById(R.id.txvDescription)
        val btnEdit: ImageView = view.findViewById(R.id.imgEditImage)
        val btnDelete: ImageView = view.findViewById(R.id.imgDeleteImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Load image into ImageView using a library like Glide or Picasso
        val imageResource = imageList[position]
        val senderId = imageResource.senderId
        holder.title.text = imageResource.title
        holder.description.text = imageResource.description
        Glide.with(context).load(imageResource.image).into(holder.imageView)

        // Set click listener or any additional logic if needed
        holder.imageView.setOnClickListener {
            // Handle item click
            // You can open a detailed view or perform any other action
            openImageExternally(context, imageResource.image)
        }
        holder.btnDownload.setOnClickListener {
            // Handle item click
            // You can open a detailed view or perform any other action
            val fileName = imageResource.image.substring(imageResource.image.lastIndexOf('/') + 1)
//            val title = "Downloading ${imageResource.title}"
            val title = "Downloading Edusphere resource"
            val description = "Please wait..."
            Toast.makeText(context, "Resource download started...", Toast.LENGTH_SHORT).show()
            downloadMedia(context, imageResource.image, fileName, title, description)
        }

        holder.btnEdit.setOnClickListener {
            // Handle item click
            // You can open a detailed view or perform any other action
            Toast.makeText(context, "Edit button clicked", Toast.LENGTH_SHORT).show()
            editImage(context, senderId)
        }
        holder.btnDelete.setOnClickListener {
            // Handle item click
            // You can open a detailed view or perform any other action
            Toast.makeText(context, "Delete button clicked", Toast.LENGTH_SHORT).show()
            deleteImage(context, senderId)
        }
    }
    private fun deleteImage(context: Context, senderId: String) {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val currentUserId = firebaseUser?.uid ?: ""
        Log.d("GroupChatAdapter", "CurrentUserId: $currentUserId, SenderId: $senderId")

        return if (senderId == currentUserId) {
            Toast.makeText(context, "Delete button clicked", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "You can only delete your own resources", Toast.LENGTH_SHORT)
                .show()
        }

    }

    private fun editImage(context: Context, senderId: String) {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val currentUserId = firebaseUser?.uid ?: ""
        Log.d("GroupChatAdapter", "CurrentUserId: $currentUserId, SenderId: $senderId")

        return if (senderId == currentUserId) {
            Toast.makeText(context, "Edit button clicked", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "You can only edit your own resources", Toast.LENGTH_SHORT)
                .show()
        }


    }

    fun downloadMedia(context: Context, mediaUrl: String, fileName: String, title: String, description: String) {
        // Create a DownloadManager request
        val request = DownloadManager.Request(Uri.parse(mediaUrl))
            .setTitle(title)
            .setDescription(description)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setAllowedOverRoaming(false)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            .setMimeType("image/*") // Set the appropriate MIME type for your media (e.g., "image/*", "video/*")

        // Get the DownloadManager service
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        // Enqueue the download and get the download ID
        val downloadId = downloadManager.enqueue(request)

        // You can use the download ID to track the status of the download, if needed
    }

   private fun openImageExternally(context: Context, imageUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(imageUrl)
        context.startActivity(intent)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}
