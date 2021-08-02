package com.example.chatapp.ChatSection

data class message(
    val message: String,
    val receiver: String,
    val sender: String
)
{
    constructor(): this("", "", "")
}
