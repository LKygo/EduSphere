package com.kygoinc.edusphere.models

data class GroupChat(
    val groupId: String = "",
    val messageId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val message: String = "",
    val timestamp: Long? = null
)