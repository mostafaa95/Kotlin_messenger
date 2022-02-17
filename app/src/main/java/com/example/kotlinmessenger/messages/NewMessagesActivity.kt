package com.example.kotlinmessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kotlinmessenger.messages.ChatLogActivity
import com.example.kotlinmessenger.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_messages.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NewMessagesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_messages)
        supportActionBar?.title = "Select User"
        fetchUsers()

    }
    companion object {
        const val USER_KEY = "USER KEY"
    }
    private fun fetchUsers(){

        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                snapshot.children.forEach{
                    val user = it.getValue(User::class.java)
                    if (user != null) {
                        adapter.add(UserItem(user))
                    }
                }
                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem
                    val intent = Intent(view.context,ChatLogActivity::class.java)
                    //intent.putExtra(USER_KEY, userItem.user.username)
                    intent.putExtra(USER_KEY,userItem.user)
                    startActivity(intent)
                    finish()
                }
                recyclerview_newmessage.adapter = adapter
            }


            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}
class UserItem(val user : User) : Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_textview_newmessages.text = user.username
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.imageView_newmessage)


    }

    override fun getLayout(): Int {
       return R.layout.user_row_new_message
    }

}