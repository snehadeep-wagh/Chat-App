package com.example.chatapp.tab_fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.ChatSection.ChatUserInfo
import com.example.chatapp.ChatSection.PersonalChatActivity
import com.example.chatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatFragment(): Fragment()
{
    val db = FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    val auth = FirebaseAuth.getInstance()
    var listOfUsers: MutableList<DataSnapshot> = mutableListOf()
    lateinit var adapterId: RecyclerView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        val listen = object: ChatListner
        {
            override fun onClick(currentUser: ChatUserInfo) {
                Intent(activity, PersonalChatActivity::class.java).also {
                    it.putExtra("ChatUser", currentUser)
                    startActivity(it)
                }
            }

        }

        adapterId = view.findViewById<RecyclerView>(R.id.chatRecycler)
        adapterId.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        adapterId.layoutManager = LinearLayoutManager(this.context)

        val phoneNumber = auth.currentUser!!.phoneNumber.toString()
//        val ref = db.reference.child("Chats").child("$phoneNumber").orderByChild("name")
        val ref = FirebaseDatabase.getInstance().getReference("Chats/$phoneNumber")
        ref.keepSynced(true)


        Log.i("ref", ref.toString())

        ref.orderByChild("name")
            .limitToLast(10)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userlist by lazy {
                        snapshot.children
                    }
                    listOfUsers = userlist.toMutableList()

                    adapterId.adapter = ChatAdapter(listOfUsers, context!!, listen)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context!!, "Something went wrong!", Toast.LENGTH_SHORT).show()
                }

            })





        return view
    }

    fun replaceList(userList: MutableList<DataSnapshot>)
    {
        for(i in userList)
        {
            listOfUsers.add(i)
        }
        Log.i("sizefun", listOfUsers.size.toString())
    }
}
