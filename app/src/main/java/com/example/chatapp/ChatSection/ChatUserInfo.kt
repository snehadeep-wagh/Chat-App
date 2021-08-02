package com.example.chatapp.ChatSection

import java.io.Serializable

data class ChatUserInfo(
    val sender: String,
    val receiver: String,
    val lastMessage: String,
    val name: String,
    val imageUrl: String
): Serializable
{
    constructor() : this("", "", "", "", "")
}