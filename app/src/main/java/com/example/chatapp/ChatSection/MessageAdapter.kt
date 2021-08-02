package com.example.chatapp.ChatSection

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.google.firebase.database.DataSnapshot
import kotlinx.android.synthetic.main.receiver_chat_layout.view.*
import kotlinx.android.synthetic.main.sender_chat_layout.view.*

class MessageAdapter(private val listOfMessage: MutableList<DataSnapshot>, private val currentPhoneNumber: String, private val messageListen: messageListner): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class MessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflateView = { layout: Int ->
            LayoutInflater.from(parent.context).inflate(layout, parent, false)
        }


        return when(viewType)
        {
            MESSAGE_SENT ->{
                MessageViewHolder(inflateView(R.layout.sender_chat_layout))
            }
            MESSAGE_RECEIVED ->{
                MessageViewHolder(inflateView(R.layout.receiver_chat_layout))
            }
            UNSUPPORTED ->{
                MessageViewHolder(inflateView(R.layout.receiver_chat_layout))
            }
            else -> {
                MessageViewHolder(inflateView(R.layout.receiver_chat_layout))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = listOfMessage[position].getValue(message::class.java)

        when(currentMessage)
        {
            is message ->{
                if(currentMessage.sender == currentPhoneNumber)
                {
                    holder.itemView.textMessageSenderId.text = currentMessage.message
                    Log.i("msg_send", currentMessage.message)
                }
                else{
                    holder.itemView.textMessageReceiverId.text = currentMessage.message
                    Log.i("msg_receive", currentMessage.message)
                }
            }
        }

        holder.itemView.setOnLongClickListener {
            val key = listOfMessage[position].key
            holder.itemView.setBackgroundResource(R.color.highlight)
            messageListen.onLongClick(position, key!!)
            return@setOnLongClickListener true
        }

        holder.itemView.setOnClickListener {
            holder.itemView.setBackgroundResource(R.color.white)
            messageListen.deselect()
        }
    }

    override fun getItemCount(): Int {
        return listOfMessage.size
    }

    override fun getItemViewType(position: Int): Int {
        return when(val item = listOfMessage[position].getValue(message::class.java))
        {
            is message ->{
                if(item.sender == currentPhoneNumber)
                {
                    MESSAGE_SENT
                }
                else
                {
                    MESSAGE_RECEIVED
                }
            }
            else ->{
                UNSUPPORTED
            }
        }
    }

    companion object {
        private const val MESSAGE_SENT = 0
        private const val MESSAGE_RECEIVED = 1
        private const val UNSUPPORTED = -1
    }
}

interface messageListner
{
    fun onLongClick(pos: Int, key: String)

    fun deselect()
}
