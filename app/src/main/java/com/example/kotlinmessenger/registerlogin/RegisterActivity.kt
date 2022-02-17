package com.example.kotlinmessenger.registerlogin
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.kotlinmessenger.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import com.example.kotlinmessenger.messages.LatestMessagesActivity
import com.example.kotlinmessenger.models.User
import java.util.*


class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        performRegister()
        already_have_account_text_view.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        add_photo_button_login.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)

        }
    }
    var selectedPhotoUri : Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)
            selected_photo_circular_register.setImageBitmap(bitmap)
            add_photo_button_login.alpha = 0f
            add_photo_textview.alpha = 0f


        }
    }

    private fun performRegister(){
        register_button_register.setOnClickListener {
            val email = email_edittext_register.text.toString()
            val password = password_edittext_register.text.toString()
            if (email.isEmpty() || password.isEmpty()){
                Toast.makeText(this,"Please enter email and password",Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        Log.d("haha",it.result.toString())
                        uploadImageToFirebaseStorage()
                    }

                }
                .addOnFailureListener{
                    Toast.makeText(this,"Failed to create user: ${it.message}",Toast.LENGTH_SHORT).show()

                }
        }

    }
    private fun uploadImageToFirebaseStorage(){
        if(selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val res = FirebaseStorage.getInstance().getReference("/images/$filename")
        res.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("haha","message added successfully")
                res.downloadUrl.addOnSuccessListener {
                    Log.d("bab","Photo $it")
                    saveUserToFirebaseDatabase("$it")
                }
            }
            .addOnFailureListener{
                Toast.makeText(this,"Faliure ${it.message}",Toast.LENGTH_LONG).show()

            }


    }
    private fun saveUserToFirebaseDatabase(profileImageUrl: String){
        val uid = FirebaseAuth.getInstance().uid ?:""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid,username_edittext_register.text.toString(),profileImageUrl)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity","User added to database finally and successfully")
                val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
    }
}