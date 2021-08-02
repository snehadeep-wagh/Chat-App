package com.example.chatapp.tab_fragments

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot

class PeopleAdapter(val context: Context, userList: MutableList<DocumentSnapshot>, private val listen: Listner): RecyclerView.Adapter<PeopleAdapter.PeopleViewHolder>() {
    private var listOfUser = userList
    private val auth = FirebaseAuth.getInstance()

    inner class PeopleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val peoplePhoto: ShapeableImageView = itemView.findViewById(R.id.people_image)
        val peopleName: TextView = itemView.findViewById(R.id.peopleName)
        val peopleInfo: TextView = itemView.findViewById(R.id.peopleInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.people_item_view, parent, false)
        return PeopleViewHolder(view)
    }

    override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
        val currentUser = listOfUser[position]


        Glide.with(context)
            .load(currentUser.get("thumbLink"))
            .error(R.drawable.default_avatar1)
            .into(holder.peoplePhoto)


        if(currentUser.get("phoneNumber").toString() == auth.currentUser!!.phoneNumber.toString())
        {
            holder.peopleName.text = "My Notes"
        }
        else
        {
            holder.peopleName.text = currentUser.get("name").toString()
            holder.peopleInfo.text = currentUser.get("info").toString()
        }
        val phoneNumber = currentUser.get("phoneNumber").toString()

        holder.itemView.setOnClickListener {
            listen.onClick(currentUser)
        }


    }

    override fun getItemCount(): Int {
        return listOfUser.size
    }
}

interface Listner
{
    fun onClick(currentUser: DocumentSnapshot)
}
