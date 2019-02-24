package com.example.chatfire

import com.example.chatfire.model.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.reciever_chat_message.view.*
import kotlinx.android.synthetic.main.sender_chat_message.view.*
import java.sql.Timestamp

class ChatSenderMessageItem(val text:String, val user: User) : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.sender_textview.text = text

        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.sender_imageview
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout() = R.layout.sender_chat_message
}