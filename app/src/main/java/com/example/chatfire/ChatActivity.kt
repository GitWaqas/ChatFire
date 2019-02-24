package com.example.chatfire

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.chatfire.model.ChatMessage
import com.example.chatfire.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {
    companion object {
        val TAG = "ChatActivity"
    }

    val adapter = GroupAdapter<ViewHolder>()

    var receiverUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        recyclerview_chat.adapter = adapter
        //pass user from intent
        receiverUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = receiverUser?.username

        listenForMessages()



        send_msg_button.setOnClickListener {
            Log.d(TAG, "sending msg... maybe")
            sendMessage()
        }
    }

    private fun listenForMessages() {
        val senderID = FirebaseAuth.getInstance().uid
        val receiverID = receiverUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$senderID/$receiverID")

        ref.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                // get all messages from db
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if (chatMessage != null) {
                    Log.d(TAG, "From listener ${chatMessage.text}")
                    if (chatMessage.senderId == FirebaseAuth.getInstance().uid) {
                        val currentUser = MessagesOverviewActivity.currentUser

                        adapter.add(ChatSenderMessageItem(chatMessage.text, currentUser!!))
                    } else {

                        adapter.add(ChatReceiverMessageItem(chatMessage.text, receiverUser!!))
                    }
                }
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onCancelled(p0: DatabaseError) {
            }


        })
    }

    private fun sendMessage() {

        //get text from field
        val inputText = message_input_edittext.text.toString()
        // logged in user
        val senderId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val receiverID = user.uid

        if (senderId == null) return
        //firedb instance
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$senderId/$receiverID").push()

        // to create db for other parties messages
        val receiverRef = FirebaseDatabase.getInstance().getReference("/user-messages/$receiverID/$senderId").push()

        val chatMessage = ChatMessage(ref.key!!, inputText, senderId, receiverID, System.currentTimeMillis() / 1000)
        ref.setValue(chatMessage).addOnSuccessListener {
            Log.d(TAG, "message fired away ${ref.key} ${chatMessage.text}")
            //clear input field
            message_input_edittext.text.clear()
            // go to last position in view
            recyclerview_chat.scrollToPosition(adapter.itemCount - 1)
        }.addOnFailureListener {
            Log.d(TAG,"${it.message}")
        }

        receiverRef.setValue(chatMessage)

        val MessagesOverviewRefSender =
            FirebaseDatabase.getInstance().getReference("/latest-messages/$senderId/$receiverID")
        MessagesOverviewRefSender.setValue(chatMessage)

        val MessagesOverviewRefReceiver =
            FirebaseDatabase.getInstance().getReference("/latest-messages/$receiverID/$senderId")
        MessagesOverviewRefReceiver.setValue(chatMessage)
    }


}
