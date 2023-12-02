package com.kygoinc.edusphere.adapters

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kygoinc.edusphere.R
import com.kygoinc.edusphere.models.ImageResource

// ImageResourceAdapter.kt
class ImageResourceAdapter(
    private val context: Context,
    private val imageList: ArrayList<ImageResource>
) :
    RecyclerView.Adapter<ImageResourceAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imgResource)
        val btnDownload: ImageView = view.findViewById(R.id.imgDownloadImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Load image into ImageView using a library like Glide or Picasso
        val imageResource = imageList[position]
        Glide.with(context).load(imageResource.image).into(holder.imageView)

        // Set click listener or any additional logic if needed
        holder.itemView.setOnClickListener {
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
