package com.kygoinc.edusphere.models

data class GroupInfo(
    val key: String = "",
    val group: String = "",
    val members: ArrayList<String> = ArrayList()
)
