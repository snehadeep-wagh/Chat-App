package com.example.chatapp.ChatSection

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityPersonalChatBinding
import com.example.chatapp.user_data.User
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_personal_chat.*

class PersonalChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPersonalChatBinding
    private val db = FirebaseDatabase.getInstance()
    private lateinit var senderphoneNumber: String
    private lateinit var receiverphoneNumber: String
    private lateinit var name: String
    private lateinit var imageUrl: String
    private val auth = FirebaseAuth.getInstance()
    private var listOfMessages: MutableList<DataSnapshot> = mutableListOf()
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var globalMenu: Menu
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_ChatPage)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_personal_chat)

        val toolbar: MaterialToolbar = binding.toolbarId
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        var currentUser1: User? = null
        var currentUser2: ChatUserInfo? = null
        
        currentUser1 = intent.getSerializableExtra("user") as User?
        currentUser2 = intent.getSerializableExtra("ChatUser") as ChatUserInfo?

        currentUser1?.let { currentUser1 ->

            if(currentUser1.phoneNumber == auth.currentUser!!.phoneNumber.toString())
            {
                binding.chatProfileName.text = "My Notes"
            }
            else
            {
                binding.chatProfileName.text = currentUser1.name
            }
            receiverphoneNumber = currentUser1.phoneNumber
            senderphoneNumber = FirebaseAuth.getInstance().currentUser!!.phoneNumber.toString()
            name = currentUser1.name
            imageUrl = currentUser1.downloadLink
            Glide.with(this)
                .load(currentUser1.downloadLink)
                .error(R.drawable.default_avatar1)
                .into(binding.chatProfileImage)
        }

        currentUser2?.let { currentUser1 ->

            binding.chatProfileName.text = currentUser2.name

            if(currentUser2.receiver == auth.currentUser!!.phoneNumber.toString())
            {
                binding.chatProfileName.text = "My Notes"
            }
            else
            {
                binding.chatProfileName.text = currentUser2.name
            }
            receiverphoneNumber = currentUser2.receiver
            senderphoneNumber = FirebaseAuth.getInstance().currentUser!!.phoneNumber.toString()
            name = currentUser1.name
            imageUrl = currentUser2.imageUrl
            Glide.with(this)
                .load(currentUser2.imageUrl)
                .error(R.drawable.default_avatar1)
                .into(binding.chatProfileImage)
        }


        binding.chatSendButton.setOnClickListener {
            val str = binding.messageEt.text.toString()
            if(str.isEmpty())
            {
                Toast.makeText(applicationContext, "Empty message cannot be sent!", Toast.LENGTH_SHORT).show()
            }
            else
            {
                sendMessage(str)
                createAndSaveChatUsers(str)
                binding.messageEt.text.clear()
            }

        }

        // Chatting section ----



        messageAdapter = MessageAdapter(listOfMessages, senderphoneNumber, listen)
        messageRV.adapter = messageAdapter
        messageRV.layoutManager = LinearLayoutManager(this)
        listenToMessage()
        Log.i("sizeitem", listOfMessages.size.toString())


    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        if (menu != null) {
            globalMenu = menu
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun createAndSaveChatUsers(msg: String)
    {
        val data = ChatUserInfo(senderphoneNumber, receiverphoneNumber, msg, name, imageUrl)
        db.reference
            .child("Chats/${senderphoneNumber}")
            .child(receiverphoneNumber)
            .setValue(data)
    }

    private fun sendMessage(msg: String)
    {
        val chatId: String = createChatId(senderphoneNumber, receiverphoneNumber)
        val textMessage = message(msg, receiverphoneNumber, senderphoneNumber)
        val uniqueId = db.reference.child("Messages/${chatId}").push().key
        db.reference
            .child("Messages/${chatId}")
            .child("$uniqueId")
            .setValue(textMessage)
    }


    private fun createChatId(senderNumber: String, receiverNumber: String ) : String
    {
        return if(senderNumber > receiverNumber) {
            senderNumber+receiverNumber
        } else {
            receiverNumber+senderNumber
        }
    }

    val listen = object : messageListner
    {
        override fun onLongClick(pos: Int, key: String) {
            val deleteId = globalMenu.findItem(R.id.deleteMessageId).setVisible(true)
            deleteId.setOnMenuItemClickListener {
                deleteMessage(pos, key)
                return@setOnMenuItemClickListener true
            }
        }

        override fun deselect() {
            globalMenu.findItem(R.id.deleteMessageId).setVisible(false)
        }
    }

    private fun deleteMessage(pos: Int, key: String) {
        val delRef = FirebaseDatabase.getInstance().reference.child("Messages/${createChatId(senderphoneNumber, receiverphoneNumber)}")
            .child(key).removeValue()

        listOfMessages.removeAt(pos)
        messageAdapter.notifyDataSetChanged()
        globalMenu.findItem(R.id.deleteMessageId).setVisible(false)
        Toast.makeText(applicationContext, "Deleted", Toast.LENGTH_SHORT).show()
    }

    private fun listenToMessage()
    {
        val ref = FirebaseDatabase.getInstance().getReference("Messages/${createChatId(senderphoneNumber, receiverphoneNumber)}")
        ref.keepSynced(true)
        ref.limitToLast(50)
            .orderByKey()
            .addChildEventListener(object : ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    addMessage(snapshot)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {

                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun addMessage(msg: DataSnapshot?) {
        if (msg != null) {
            listOfMessages.add(msg)
        }

        messageAdapter.notifyItemInserted(listOfMessages.size - 1)
        messageRV.scrollToPosition(listOfMessages.size - 1)
    }


}