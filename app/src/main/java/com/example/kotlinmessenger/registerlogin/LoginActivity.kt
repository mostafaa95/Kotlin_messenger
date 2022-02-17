package com.example.kotlinmessenger.registerlogin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinmessenger.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import com.example.kotlinmessenger.messages.LatestMessagesActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        back_to_register_textview.setOnClickListener { finish() }
        login_button_login.setOnClickListener {
            val email = email_edittext_login.text.toString()
            val password = password_edittext_login.text.toString()
            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(this,"Please Enter Email and Password",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        val intent = Intent(this, LatestMessagesActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }


                }
                .addOnFailureListener{
                    Toast.makeText(this,"${it.message}",Toast.LENGTH_LONG).show()

                }
        }
    }
}