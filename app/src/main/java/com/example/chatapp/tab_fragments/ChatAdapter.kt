package com.example.chatapp.tab_fragments

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.ChatSection.ChatUserInfo
import com.example.chatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot

class ChatAdapter(val chats: MutableList<DataSnapshot>, val context: Context, val chatListen: ChatListner): RecyclerView.Adapter<ChatAdapter.ChatViewHolder>()
{
    val auth = FirebaseAuth.getInstance()
    val listOfChats: MutableList<DataSnapshot> = chats


    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val peopleName: TextView = itemView.findViewById<TextView>(R.id.peopleName)
        val lastMessage: TextView = itemView.findViewById(R.id.peopleInfo)
        val peopleImg: ImageView= itemView.findViewById(R.id.people_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.people_item_view, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val currentUser = listOfChats[position].getValue(ChatUserInfo::class.java)
        holder.peopleName.text = currentUser!!.name
        holder.lastMessage.text = currentUser!!.lastMessage

        if(currentUser.receiver == auth.currentUser!!.phoneNumber.toString())
        {
            holder.peopleName.text = "My Notes"
        }

        val imageLink = currentUser!!.imageUrl
        Glide.with(context)
            .load(imageLink).
            error(R.drawable.default_avatar1)
            .into(holder.peopleImg)

        holder.itemView.setOnClickListener {
            chatListen.onClick(currentUser)
        }

    }

    override fun getItemCount(): Int {
        return listOfChats.size
    }

}

interface ChatListner
{
    fun onClick(currentUser: ChatUserInfo)
}