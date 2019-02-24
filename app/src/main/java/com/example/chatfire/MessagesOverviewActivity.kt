package com.example.chatfire

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.chatfire.NewMessageActivity.Companion.USER_KEY
import com.example.chatfire.model.ChatMessage
import com.example.chatfire.model.MessagesOverviewItem
import com.example.chatfire.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_messages_overview.*

class MessagesOverviewActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatActivity"
        var currentUser: User? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages_overview)
        supportActionBar?.title = "Conversations"

        recyclerview_messages_overview.adapter = adapter

        // stupid long code to get vertical line, maybe add it from xml later
        recyclerview_messages_overview.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

// click listener for recycler in overview
        adapter.setOnItemClickListener { item, view ->
            Log.d(TAG, "clicked on something in the recycler view")
            val intent = Intent(this, ChatActivity::class.java)

            val listItem = item as MessagesOverviewItem
            // listItem.OtherChatParty
            intent.putExtra(USER_KEY, listItem.OtherChatParty)
            startActivity(intent)
        }

        // listen for messages and childs on node from FirebaseDB
        listenForMessages()

        fetchCurrentUSer()
        verifyUserIsLogged()
    }

    val adapter = GroupAdapter<ViewHolder>()

// hash map for messages overview

    val MessagesOverview = HashMap<String, ChatMessage>()

    private fun refreshRecyclerViewMessagesOverview() {

        // clear old messages first

        adapter.clear()

        MessagesOverview.values.forEach {

            adapter.add(MessagesOverviewItem(it))

        }
    }

    private fun listenForMessages() {
        val senderId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$senderId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return

                MessagesOverview[p0.key!!] = chatMessage
                refreshRecyclerViewMessagesOverview()

                //  adapter.add(MessagesOverviewItem(chatMessage))

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return

                MessagesOverview[p0.key!!] = chatMessage
                refreshRecyclerViewMessagesOverview()

                /* val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                adapter.add(MessagesOverviewItem(chatMessage))*/
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })
    }


    private fun fetchCurrentUSer() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
                Log.d(TAG, "Current user is: ${currentUser?.profileImageUrl}")
            }

        })
    }

    private fun verifyUserIsLogged() {
        val uid = FirebaseAuth.getInstance().uid

        // if no user go back
        if (uid == null) {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.new_message -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                // intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
            R.id.sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
