package com.example.message.recyclerview.item

import android.content.Context
import com.example.message.R
import com.example.message.glide.GlideApp
import com.example.message.model.User
import com.example.message.controller.StorageController
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_person.*


class PersonItem(val person: User,
                 val userId: String,
                 private val context: Context)
    : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView_name.text = person.name
        viewHolder.textView_bio.text = person.bio
        if (person.avatarPath != null)
            GlideApp.with(context)
                .load(StorageController.pathToReference(person.avatarPath))
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .into(viewHolder.imageView_profile_picture)
    }

    override fun getLayout() = R.layout.item_person
}