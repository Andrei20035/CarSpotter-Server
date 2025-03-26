package com.carspotter.data.table

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object FriendRequests: Table("friend_requests") {
    val senderId = integer("sender_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val receiverId = integer("receiver_id").references(Users.id, onDelete = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(senderId, receiverId)
}