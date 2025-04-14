package com.carspotter.data.table

import org.jetbrains.exposed.sql.Table

object AuthCredentials: Table("auth_credentials") {
    val id = integer("id").autoIncrement()
    val email = varchar("email", 255).uniqueIndex()
    val password = text("password").nullable()
    val provider = varchar("provider", 20).default("local")
    val googleId = varchar("google_id", 100).nullable()

    override val primaryKey = PrimaryKey(id)
}