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
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.kygoinc.edusphere.R
import com.kygoinc.edusphere.models.PdfResource

class PdfResourceAdapter(private val context: Context, private val pdfList: ArrayList<PdfResource>) :
    RecyclerView.Adapter<PdfResourceAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val pdfName: TextView = view.findViewById(R.id.txtDocumentTitle)
//        val pdfIcon: ImageView = view.findViewById(R.id.imgPdfIcon)
        val cardView: CardView = view.findViewById(R.id.cardDocument)
        val btnDownload : ImageView = view.findViewById(R.id.imgDownloadDocument)
        val pdfDescription : TextView = view.findViewById(R.id.txtDocumentDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_document, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pdfResource = pdfList[position]

        // Set PDF name
        holder.pdfName.text = pdfResource.title
        holder.pdfDescription.text = pdfResource.description

        // Set PDF icon
//        holder.pdfIcon.setImageResource(R.drawable.ic_pdf_icon)

        // Set click listener or any additional logic if needed
        holder.cardView.setOnClickListener {
            // Handle item click
            // You can open the PDF or perform any other action
            openDocumentExternally(context, pdfResource.document)
        }
        holder.btnDownload.setOnClickListener {
            // Handle item click
            // You can open a detailed view or perform any other action
            val fileName = pdfResource.document.substring(pdfResource.document.lastIndexOf('/') + 1)
//            val title = "Downloading ${pdfResource.title}"

            downloadDocument(context, pdfResource.document, fileName)

        }
    }

    private fun openDocumentExternally(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.parse(url), "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        context.startActivity(intent)
    }

    private fun downloadDocument(context: Context, documentUrl: String, fileName: String) {
        try {
            // Create a DownloadManager request
            val request = DownloadManager.Request(Uri.parse(documentUrl))
                .setTitle(fileName) // Set the file name as the title
                .setDescription("Downloading PDF...") // Add a description if needed
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$fileName.pdf")
                .setMimeType("application/pdf") // Set the MIME type for PDF documents

            // Get the DownloadManager service
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            // Enqueue the download and get the download ID
            downloadManager.enqueue(request)
        } catch (e: Exception) {
            // Handle exceptions
            e.printStackTrace()
            Toast.makeText(context, "Error downloading PDF", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return pdfList.size
    }
}