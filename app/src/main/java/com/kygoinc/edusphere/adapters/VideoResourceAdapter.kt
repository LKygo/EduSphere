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
import com.kygoinc.edusphere.models.VideoResource

// ImageResourceAdapter.kt
class VideoResourceAdapter(
    private val context: Context,
    private val videoList: ArrayList<VideoResource>
) :
    RecyclerView.Adapter<VideoResourceAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imgResource)
        val btnPlay: ImageView = view.findViewById(R.id.playVideo)
        val btnDownload: ImageView = view.findViewById(R.id.imgDownloadVideo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Load image into ImageView using a library like Glide or Picasso
        val videoResource = videoList[position]
        Glide.with(context).load(videoResource.url).into(holder.imageView)

        // Set click listener or any additional logic if needed
        holder.btnPlay.setOnClickListener {
            // Handle item click
            // You can open a detailed view or perform any other action
            openVideoExternally(context, videoResource.url)
        }
        holder.btnDownload.setOnClickListener {
            // Handle item click
            // You can open a detailed view or perform any other action
            val fileName = videoResource.url.substring(videoResource.url.lastIndexOf('/') + 1)
//            val title = "Downloading ${videoResource.title}"
            val title = "Downloading Edusphere resource"
            val description = "Please wait..."
            downloadMedia(context, videoResource.url, fileName, title, description)

            Toast.makeText(context, "Resource download started...", Toast.LENGTH_SHORT).show()
        }
    }

    fun downloadMedia(
        context: Context,
        mediaUrl: String,
        fileName: String,
        title: String,
        description: String
    ) {
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

        // Use the download ID to track the status of the download, if needed
    }


    private fun openVideoExternally(context: Context, videoUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.parse(videoUrl), "video/*")
        context.startActivity(intent)
    }

    override fun getItemCount(): Int {
        return videoList.size
    }
}
