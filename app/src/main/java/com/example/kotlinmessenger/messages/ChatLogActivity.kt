package com.example.kotlinmessenger.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kotlinmessenger.NewMessagesActivity
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.models.ChatMessage
import com.example.kotlinmessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.textView_chat_to_chatlog

class ChatLogActivity : AppCompatActivity() {
    val adapter = GroupAdapter<ViewHolder>()
    var fromUser : User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        fromUser = intent.getParcelableExtra<User>(NewMessagesActivity.USER_KEY)
        supportActionBar?.title = fromUser?.username
        recyclerview_chatlog.adapter = adapter
        send_button_chatlog.setOnClickListener {
            performSendMessage()
        }
        listenForChanges()
    }
    private fun listenForChanges(){
        val fromId = FirebaseAuth.getInstance().uid
        val toId = fromUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("user-messages/$toId/$fromId")
        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if(chatMessage != null){
                    if(chatMessage.fromId == FirebaseAuth.getInstance().uid){
                        val currentUser = LatestMessagesActivity.currentUser ?: return
                        adapter.add(ChatFromItem(chatMessage.text,currentUser))
                    }else{
                        adapter.add(ChatToItem(chatMessage.text,fromUser!!))
                    }
                }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun performSendMessage(){
        val text = editText_chat_log.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessagesActivity.USER_KEY)
        val toId = user?.uid
        //val reference = FirebaseDatabase.getInstance().getReference("messages").push()
        val reference = FirebaseDatabase.getInstance().getReference("user-messages/$toId/$fromId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("user-messages/$fromId/$toId").push()
        if (fromId == null) return
        if(toId == null) return
        val chatMessage = ChatMessage(reference.key!!,text,fromId,toId,System.currentTimeMillis() / 1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                editText_chat_log.text.clear()
                recyclerview_chatlog.scrollToPosition(adapter.itemCount-1)
            }
        toReference.setValue(chatMessage)
        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)
        val latestToMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestToMessageRef.setValue(chatMessage)

    }
}
class ChatFromItem(val text: String, private val user: User) : Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.texview_chat_from_chat_log.text = text
        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageView_chat_from_chatlog
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

}
class ChatToItem(val text : String, private val user: User) : Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_chat_to_chatlog.text = text
        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageView_to_chatlog
        Picasso.get().load(uri).into(targetImageView)

    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

}