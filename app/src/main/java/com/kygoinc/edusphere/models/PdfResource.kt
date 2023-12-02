package com.kygoinc.edusphere.models

data class PdfResource(
    val title: String = "", // PDF file name
    val description: String = "", // PDF file description
    val document: String = "" // URL or path to the PDF file
)