package com.example.chatfire

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import org.jetbrains.anko.intentFor


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

       // old tedious boilerplate
        /*val intent = Intent(this, MessagesOverviewActivity::class.java)
        startActivity(intent)
        finish()*/

        // or the anko way :)
        startActivity(intentFor<MessagesOverviewActivity>())
    }
}
