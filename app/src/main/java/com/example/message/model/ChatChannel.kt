package com.example.message.model

data class ChatChannel(val userIds: MutableList<String>) {
    constructor() : this(mutableListOf())
}