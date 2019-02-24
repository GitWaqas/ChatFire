package com.example.chatfire.view

import com.example.chatfire.R
import com.example.chatfire.model.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.useritem_row.view.*

class UserItem(val user: User) : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_textview.text = user.username

        //set avatar from db

        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.avatar_imageview_overview)
    }

    override fun getLayout() = R.layout.useritem_row
}


