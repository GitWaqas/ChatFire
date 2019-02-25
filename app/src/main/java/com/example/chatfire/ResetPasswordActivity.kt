package com.example.chatfire

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class ResetPasswordActivity : AppCompatActivity() {

    companion object {
        val TAG = "ResetPasswordActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_reset_password)


        // anko DSL

        verticalLayout {
            val email = editText { hint = "Email goes here..." }
            button("Submit") {
                onClick {
                    if (!email.text.isEmpty()){

                        TODO("not implemented")

                        /*FirebaseAuth.getInstance()!!.sendPasswordResetEmail(email.text.toString())
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    toast("Email sent.")
                                    Log.d(TAG, task.result.toString())
                                    // back to login
                                    startActivity(intentFor<LoginActivity>().clearTop())
                                } else {
                                    Log.d(TAG, task.exception!!.message)
                                    Log.d(TAG,email.text.toString())
                                }
                            }*/
                    } else {
                        toast("Missing field")
                    }

                }
            }
        }
    }


}
