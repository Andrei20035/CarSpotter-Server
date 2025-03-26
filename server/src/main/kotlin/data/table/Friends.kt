package com.carspotter.data.table

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Friends: Table("friends") {
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val friendId = integer("friend_id").references(Users.id, onDelete = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(userId, friendId)

    init {
        check("chk_no_self_friendship") { userId neq friendId }
    }

}