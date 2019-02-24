package com.example.chatfire.model

import com.example.chatfire.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.overview_messages.view.*

class MessagesOverviewItem(val chatMessage: ChatMessage) : Item<ViewHolder>() {

    var OtherChatParty : User? = null
    override fun getLayout() = R.layout.overview_messages

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.lastmessage_textview_overview.text = chatMessage.text


        val chatP: String

        when {
            chatMessage.senderId == FirebaseAuth.getInstance().uid -> chatP = chatMessage.receiverId
            else -> chatP = chatMessage.senderId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatP")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                OtherChatParty = p0.getValue(User::class.java)
                viewHolder.itemView.username_textview_overview.text = OtherChatParty?.username
                // set the avatar on message overview
                Picasso.get().load(OtherChatParty?.profileImageUrl).into(viewHolder.itemView.avatar_imageview_overview)
            }

            override fun onCancelled(p0: DatabaseError) {

            }


        })
    }

}