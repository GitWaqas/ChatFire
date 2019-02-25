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
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.getInstance
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import java.util.*
import kotlin.collections.ArrayList


class RegisterActivity : AppCompatActivity() {

    companion object {
        val TAG = "RegisterActivity"
        val RC_SIGN_IN = 123
    }

    private lateinit var auth: FirebaseAuth


    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        // Initialize Firebase Auth
        auth = getInstance()!!


        val providers = ArrayList<AuthUI.IdpConfig>()
        providers.add(AuthUI.IdpConfig.EmailBuilder().build())
        providers.add(AuthUI.IdpConfig.GoogleBuilder().build())


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        /*startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(), 123
        )*/



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
            Log.d("Main", "should show LoginActivity")
            startActivity(intentFor<LoginActivity>())
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

        // show progress because it takes a while  (must find a non deprecated method later)

        indeterminateProgressDialog("Please hold on…")

        // Firebase email sign in auth
        getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                // if success show created user in debug
                Log.d(TAG, "User created with uid: ${it.result.user.uid}")
                // upload avatar
                uploadImageToFirebase()
            }.addOnFailureListener {
                Log.d(TAG, "Failed to create user: ${it.message}")
                Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_LONG).show()
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
        val uid = getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid, username_editText.text.toString(), profileImageUrl)

        // save User object
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("Register", "saved to DB")

                /* val intent = Intent(this, MessagesOverviewActivity::class.java)
                 intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                 startActivity(intent)*/
                // flush intent history so back button navigates to android launcher
                startActivity(intentFor<MessagesOverviewActivity>().clearTask().newTask())
            }
            .addOnFailureListener {
                Log.d(TAG, "${it.message}")
            }
    }

}

