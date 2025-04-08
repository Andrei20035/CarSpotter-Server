package com.carspotter.data.table

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object Friends : Table("friends") {
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val friendId = integer("friend_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)


    override val primaryKey = PrimaryKey(userId, friendId)

    init {
        check("chk_no_self_friendship") { userId neq friendId }
    }

}