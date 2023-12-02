package com.kygoinc.edusphere.models


data class ImageResource(
    val image: String = "",  // URL of the image
    val senderId: String = "",  // ID of the sender
    val tags: List<String> = emptyList()
    // Add any other fields you want to include
)


