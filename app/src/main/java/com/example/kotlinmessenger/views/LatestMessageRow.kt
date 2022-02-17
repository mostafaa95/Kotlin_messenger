package com.example.kotlinmessenger.views

import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.models.ChatMessage
import com.example.kotlinmessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageRow(private val chatMessage: ChatMessage) : Item<ViewHolder>(){
    var chatPartner : User? = null
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.message_content_textView_latest_message_row.text = chatMessage.text
        val chatPartnerId : String
        if(FirebaseAuth.getInstance().uid == chatMessage.fromId){
            chatPartnerId = chatMessage.toId
        } else{
            chatPartnerId = chatMessage.fromId
        }
        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatPartner = snapshot.getValue(User::class.java)
                viewHolder.itemView.username_textView_latesMessageRow.text = chatPartner?.username
                val targetImage = viewHolder.itemView.imageView_latestmessage_row
                Picasso.get().load(chatPartner?.profileImageUrl).into(targetImage)
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    override fun getLayout(): Int {
        return  R.layout.latest_message_row
    }

}