package com.kygoinc.edusphere.models

data class User(
    val uid: String = "",
    val username: String = "",
    val profile: String = "",
    val about: String = "",
    val status: String = "",
    val interests: String = "",
    val search: String = "",
    val phone: String = ""
)