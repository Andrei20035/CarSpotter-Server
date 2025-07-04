package com.carspotter.data.table

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object FriendRequests : Table("friend_requests") {
    val senderId = uuid("sender_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val receiverId = uuid("receiver_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)

    override val primaryKey = PrimaryKey(senderId, receiverId)
}