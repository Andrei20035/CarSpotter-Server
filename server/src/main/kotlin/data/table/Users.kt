package com.carspotter.data.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

object Users : Table("users") {
    val id = integer("id").autoIncrement()
    val profilePicturePath = text("profile_picture_path").nullable()
    val firstName = varchar("first_name", 80)
    val lastName = varchar("last_name", 80)
    val birthDate = date("birth_date")
    val username = varchar("username", 50)
    val password = text("password")
    val country = varchar("country", 50)
    val spotScore = integer("spot_score").default(0)

    override val primaryKey = PrimaryKey(id)
}
