package com.example.message

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (FirebaseAuth.getInstance().currentUser == null){
            startActivity(intentFor<SignInActivity>().clearTask().newTask())
        }else{
            startActivity(intentFor<MainActivity>().clearTask().newTask())
            finish()
        }
    }
}
