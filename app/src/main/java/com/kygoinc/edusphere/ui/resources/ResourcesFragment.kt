package com.kygoinc.edusphere.ui.resources

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.nfc.Tag
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.kygoinc.edusphere.R
import com.kygoinc.edusphere.adapters.ResourcesViewPagerAdapter
import com.kygoinc.edusphere.databinding.FragmentResourcesBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ResourcesFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var progressDialog: ProgressDialog
    private var _binding: FragmentResourcesBinding? = null
    private val REQUEST_GALLERY_PERMISSION = 22
    private val REQUEST_IMAGE_CAPTURE = 39
    private val REQUEST_DOCUMENT_PERMISSION = 33
    private var filePath: Uri? = null
    private var galleryImageUri: Uri? = null
    private var galleryVideoUri: Uri? = null
    private var documentUri: Uri? = null
    private var tags: String? = null
    private val uid = FirebaseAuth.getInstance().uid
    private var title : String = ""
    private var description : String = ""

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentResourcesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.fabUploadResource.setOnClickListener {
            showResourceUploadDialog()
        }

//        Inflate progress dialog
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Uploading your resource...")

        return root


    }

    private fun showResourceUploadDialog() {
        val dialogView = layoutInflater.inflate(R.layout.resource_selector, null)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val btnCamera = dialogView.findViewById<ImageView>(R.id.btn_camera)
        val btnGallery = dialogView.findViewById<ImageView>(R.id.btn_gallery)
        val btnDocument = dialogView.findViewById<ImageView>(R.id.btn_document)

        btnCamera.setOnClickListener {
            // Handle camera button click
            requestCameraPermission()
//            requestExternalStorageWritePermission()
            alertDialog.dismiss()

        }
        btnGallery.setOnClickListener {
            // Handle Gallery button click
            Log.d("gallery", "showResourceUploadDialog: called to request gallery permission")
//            requestGalleryPermission()
            pickMedia()
            alertDialog.dismiss()

        }

        btnDocument.setOnClickListener {
            // Handle gallery button click
            openDocumentPicker()
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun pickMedia() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/* video/*"
        startActivityForResult(intent, REQUEST_GALLERY_PERMISSION)
    }

    private fun requestDocumentPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_DOCUMENT_PERMISSION
            )
        } else {
            openDocumentPicker()
        }
    }

    private fun openDocumentPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
//        Include document
        intent.type = "application/pdf"

        startActivityForResult(intent, REQUEST_DOCUMENT_PERMISSION)
    }


    //    Handle camera requests
    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.CAMERA),
                REQUEST_IMAGE_CAPTURE
            )
        } else {
            openCamera()
        }
    }

    private fun openCamera() {

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.CAMERA),
                REQUEST_IMAGE_CAPTURE
            )
        }

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                val storageDirs = ContextCompat.getExternalFilesDirs(
                    requireContext(), Environment.DIRECTORY_PICTURES
                )
                val storageDir = storageDirs[0]

                val photoFile: File? = File(
                    storageDir, "JPEG_${
                        SimpleDateFormat(
                            "yyyyMMdd_HHmmss", Locale.getDefault()
                        ).format(Date())
                    }.jpg"
                ).apply {
                    createNewFile()
                }

                photoFile?.let {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(), "com.kygoinc.edusphere.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    // Save the file path
                    filePath = photoURI
                    if (filePath != null) {
                        Log.d("camera", "openCamera: $photoURI")
                        showTagsDialog("camera")
                    }
//                        binding.imgJobCardValidation.setBackgroundResource(R.drawable.check)
                }
            }
        }
    }

    private fun showTagsDialog(filetype: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_tags, null)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val editTitle = dialogView.findViewById<EditText>(R.id.editResourceTitle)
        val editDescription = dialogView.findViewById<EditText>(R.id.editResourceDescription)
        val editTextTags = dialogView.findViewById<EditText>(R.id.editTextTags)

        val buttonSave = dialogView.findViewById<Button>(R.id.buttonSave)

        buttonSave.setOnClickListener {
            // Get the entered tags
            tags = editTextTags.text.toString()
            title = editTitle.text.toString()
            description = editDescription.text.toString()

            Log.d("docTags", "Entered tags: $tags")
            Log.d("docTags", "Entered tags: $documentUri")

            if ( title.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please enter a title",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (tags!!.isEmpty() || title.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please enter at least one tag",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            when (filetype) {
                "camera" -> uploadImageToFirebaseStorage(filePath!!,tags!!.split(","))
                "image" -> uploadImageToFirebaseStorage(galleryImageUri!!, tags!!.split(","))
                "video" -> uploadVideoToFirestore(galleryVideoUri!!, tags!!.split(","))
                "document" -> uploadDocumentToFirebaseStorage(documentUri!!, tags!!.split(","))
            }
            progressDialog.show()
            // Now you can use the tags for your logic (e.g., save to Firebase)
            Log.d("docTags", "Entered tags: $documentUri")
            Log.d("docTags", "Entered tags: $tags")

            // Dismiss the dialog
            alertDialog.dismiss()
        }

        alertDialog.show()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
//        REQUEST_DOCUMENT_PERMISSION -> {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted, open camera
//                openDocumentPicker()
//            } else {
//                // Permission denied, show toast and request permission again when required
//                Toast.makeText(
//                    requireContext(),
//                    "Permission to read external storage required",
//                    Toast.LENGTH_SHORT
//                ).show()
//
//                ActivityCompat.requestPermissions(
//                    requireActivity(),
//                    arrayOf(android.Manifest.permission.CAMERA),
//                    REQUEST_DOCUMENT_PERMISSION
//                )
//
//            }
//        }

            REQUEST_IMAGE_CAPTURE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, open camera
                    openCamera()
                } else {
                    // Permission denied, show toast and request permission again when required
                    Toast.makeText(
                        requireContext(),
                        "Camera permission required to take pictures",
                        Toast.LENGTH_SHORT
                    ).show()

                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(android.Manifest.permission.CAMERA),
                        REQUEST_IMAGE_CAPTURE
                    )

                }
            }
        }
    }

    //    Differentiate between video and image from gallery
    private fun getMediaType(uri: Uri): String? {
        val contentResolver = requireContext().contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        val type = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))

        return when (type) {
            "jpg", "jpeg", "png", "gif", "bmp" -> "image"
            "mp4", "3gp", "mkv", "webm" -> "video"
            else -> null // Other types can be handled as needed
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_GALLERY_PERMISSION -> {
                if (resultCode == Activity.RESULT_OK) {
                    // Media selected, obtain its URI
                    data?.data?.let { mediaUri ->
                        val mediaType = getMediaType(mediaUri)
                        when (mediaType) {
                            "image" -> {
                                // Handle image
                                Log.d("Gallery", "Selected Image URI: $mediaUri")
                                galleryImageUri = mediaUri
                                showTagsDialog("image")
                            }

                            "video" -> {
                                // Handle video
                                Log.d("Gallery", "Selected Video URI: $mediaUri")
                                galleryVideoUri = mediaUri
                                showTagsDialog("video")
                            }

                            else -> {
                                // Handle other types if needed
                                Toast.makeText(
                                    requireContext(),
                                    "Unsupported file type",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.d("Gallery", "Selected Media URI: $mediaUri")
                            }
                        }
                    }
                }
            }

            REQUEST_DOCUMENT_PERMISSION -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    documentUri = data.data!!

                    // Now you can upload the document to Firebase or handle it as needed
                    Log.d("Document", "Selected Document URI: $documentUri")

                    showTagsDialog("document")

//                    uploadDocumentToFirebaseStorage(documentUri)
                }
            }
        }
    }

    //    Firebase function to upload to storage
// Firebase function to upload image to storage
    private fun uploadImageToFirebaseStorage(imageUri: Uri, tags: List<String>) {
        val filename = UUID.randomUUID().toString()
        val storageRef = FirebaseStorage.getInstance().getReference("/images/$filename")

        storageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    if (uid != null) {
                        saveImageToDatabase(downloadUrl.toString(),title, description, tags, uid)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Failed to upload image to Firebase Storage: $exception")
            }
    }

    // Save image information to the Realtime Database
    private fun saveImageToDatabase(downloadUrl: String, title: String, description: String, tags: List<String>, uid: String) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("/resources/images").push()

        val resourceData = hashMapOf(
            "image" to downloadUrl,
            "senderId" to uid,
            "title" to title,
            "description" to description,
            "tags" to tags
            // Add any other information you want to store
        )

        databaseRef.setValue(resourceData)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Log.d("Firebase", "Image resource stored successfully")
                Toast.makeText(
                    requireContext(),
                    "Image resource stored successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Log.e("Firebase", "Failed to store image information: $it")
            }
    }

    //    Upload video to firebase storage
    private fun uploadVideoToFirestore(videoUri: Uri, tags: List<String>) {
        // Generate a unique ID for the video entry
        val videoId = UUID.randomUUID().toString()

        // Get a reference to the Firebase Storage "videos" folder
        val storageReference = FirebaseStorage.getInstance().getReference("videos")

        // Create a reference for the video file in Firebase Storage
        val videoFileReference = storageReference.child("$videoId.mp4")

        // Upload the video file to Firebase Storage
        videoFileReference.putFile(videoUri)
            .addOnSuccessListener {
                // Video upload success, get the download URL
                videoFileReference.downloadUrl.addOnSuccessListener { downloadUri ->
                    // Video download URL obtained, now store the entry in the Realtime Database
                    if (uid != null) {
                        storeVideoEntryInDatabase(videoId, downloadUri.toString(), title, description, tags, uid)
                    }
                }.addOnFailureListener { exception ->
                    // Handle failure to get video download URL
                    Log.e("VideoUpload", "Failed to get video download URL: $exception")
                }
            }
            .addOnFailureListener { exception ->
                // Handle failure to upload the video
                Log.e("VideoUpload", "Failed to upload video: $exception")
            }
    }

    private fun storeVideoEntryInDatabase(
        videoId: String,
        videoUrl: String,
        title: String,
        description: String,
        tags: List<String>,
        uid: String
    ) {
        // Get a reference to the Firebase Realtime Database "resources" node
        val databaseReference = FirebaseDatabase.getInstance().getReference("resources")

        // Create a new entry for the video
        val videoEntry = hashMapOf(
            "type" to "video",
            "url" to videoUrl,
            "title" to title,
            "description" to description,
            "senderId" to uid, // Replace with the actual sender ID
            "tags" to tags
        )

        // Set the video entry in the "videos" child node
        databaseReference.child("videos").child(videoId).setValue(videoEntry)
            .addOnSuccessListener {
                // Video entry stored successfully
                progressDialog.dismiss()
                Toast.makeText(
                    requireContext(),
                    "Video entry stored successfully",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("VideoUpload", "Video entry stored successfully")
            }
            .addOnFailureListener { exception ->
                // Handle failure to store video entry
                progressDialog.dismiss()
                Toast.makeText(
                    requireContext(),
                    "Failed to store video entry: $exception",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("VideoUpload", "Failed to store video entry: $exception")
            }
    }

    private fun uploadDocumentToFirebaseStorage(documentUri: Uri, tags: List<String>) {
        val filename = UUID.randomUUID().toString()
        val storageRef = FirebaseStorage.getInstance().getReference("/documents/$filename")

        storageRef.putFile(documentUri)
            .addOnSuccessListener { taskSnapshot ->
                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    if (uid != null) {
                        saveDocumentToDatabase(downloadUrl.toString(), title, description, tags, uid)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Failed to upload document to Firebase Storage: $exception")
            }
    }

    private fun saveDocumentToDatabase(downloadUrl: String, title : String, description: String, tags: List<String>, uid: String) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("/resources/documents").push()

        val resourceData = hashMapOf(
            "document" to downloadUrl,
            "senderId" to uid,
            "title" to title,
            "description" to description,
            "tags" to tags,
            // Add any other information you want to store
        )

        databaseRef.setValue(resourceData)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Log.d("Firebase", "Document resource stored successfully")
                Toast.makeText(
                    requireContext(),
                    "Document resource stored successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Log.e("Firebase", "Failed to store document information: $it")
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabLayout = binding.tabLayout
        viewPager = binding.viewPager
        val pagerAdapter =
            ResourcesViewPagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Images"
                1 -> "Videos"
                2 -> "Documents"
                else -> null
            }

        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}