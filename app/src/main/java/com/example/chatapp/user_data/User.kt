package com.example.chatapp.`user_data`

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ServerTimestamp
import com.google.type.Date
import java.io.Serializable

class User(
    var name: String = "",
    var phoneNumber: String = "",
    var downloadLink: String = "",
    var ThumbLink: String = "",
    var info: String = "",
//    var onlineStatus: FieldValue = FieldValue.serverTimestamp()

): Serializable
//{
//    constructor():this("", "", "", "", "", FieldValue.serverTimestamp())
//
//
//    constructor(name: String, phoneNumber: String, downloadLink: String, ThumbLink: String, info: String) : this(
//                name,
//                phoneNumber,
//                downloadLink,
//                ThumbLink,
//                info,
//                FieldValue.serverTimestamp()
//    )
//}