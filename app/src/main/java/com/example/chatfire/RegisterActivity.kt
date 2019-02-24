package com.example.chatfire

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.example.chatfire.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*


class RegisterActivity : AppCompatActivity() {

    companion object {
        val TAG = "RegisterActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        photo_button.setOnClickListener {
            Log.d(TAG, "should open photo select menu")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        register_button.setOnClickListener {
            registerUser()
        }

        have_account_textView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            Log.d("Main", "should show LoginActivity")
        }
    }

    var photoUri: Uri? = null


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d(TAG, "photo selected")
            photoUri = data.data

            //gets image as bitmap
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
            // set bitmap to circle
            select_photo_cirle.setImageBitmap(bitmap)
            // make button transparent
            photo_button.alpha = 0f
        }
    }


    private fun registerUser() {
        val email = email_editText.text.toString()
        val password = password_editText.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Missing fields", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG, "create user with mail: $email")


        // Firebase email sign in auth
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                // if success show created user in debug
                Log.d(TAG, "User created in with uid: ${it.result.user.uid}")

                // upload avatar

                uploadImageToFirebase()
            }.addOnFailureListener {
                Log.d(TAG, "Failed to create user: ${it.message}")
                Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImageToFirebase() {
        if (photoUri == null) return
        //generate random image name
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(photoUri!!)
            .addOnSuccessListener {
                Log.d(TAG, "img upload: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {

                    Log.d(TAG, "location of file: ${it}")
                    saveUserToDB(it.toString())
                }
            }.addOnFailureListener {
                Log.d(TAG, "something went wrong: ${it.message}")
            }
    }

    private fun saveUserToDB(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid, username_editText.text.toString(), profileImageUrl)

        // save User object
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("Register", "saved to DB")
                val intent = Intent(this, MessagesOverviewActivity::class.java)
                // flush intent history so back button navigates to android launcher
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d(TAG, "${it.message}")
            }
    }

}

