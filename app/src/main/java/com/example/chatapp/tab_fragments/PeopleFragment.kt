package com.example.chatapp.tab_fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.ChatSection.PersonalChatActivity
import com.example.chatapp.R
import com.example.chatapp.user_data.User
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PeopleFragment : Fragment() {
    private val db = FirebaseFirestore.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_people, container, false)
        val adapterId = view.findViewById<RecyclerView>(R.id.peopleAdapterId)
        adapterId.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        adapterId.layoutManager = LinearLayoutManager(context)

        val listen = object : Listner {
            override fun onClick(currentUser: DocumentSnapshot) {
                val obj: User? = currentUser.toObject(User::class.java)
                val intent = Intent(activity, PersonalChatActivity::class.java)
                intent.putExtra("user", obj)
                startActivity(intent)
            }

        }

        db.collection("Users")
            .orderBy("name", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener {

                val adapter = PeopleAdapter(context!!, it!!.documents, listen)
                adapterId.adapter = adapter

            }
            .addOnFailureListener {
                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
            }

        return view
    }

}
